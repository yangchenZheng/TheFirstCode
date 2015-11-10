package com.app.imageloader.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 加载图片
 * Created by zhengyangchen on 2015/11/3.
 */
public class ImageLoader {

    /**
     * 单例下的实例
     */
    private static ImageLoader mInstance;

    /**
     * 缓存的核心对象
     */
    private LruCache<String, Bitmap> mLruCache;

    /**
     * 线程池
     */
    private ExecutorService mThreadPool;

    /**
     * 默认的线程数
     */
    private static final int DEFAULT_THREAD_COUNT = 1;

    /**
     * 对列的调度方式
     */
    private Type mType = Type.FIFO;

    /**
     * 存放调度方式
     */
    public enum Type {
        LIFO, FIFO
    }

    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQueue;

    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;
    /**
     * 用于通知后台轮询线程进行轮询的handler
     */
    private Handler mPoolThreadHandler;
    /**
     * UI线程中的handler
     */
    private Handler mUIHandler;
    /**
     * 信号量，用于解决多线程并发的问题，初始化为0，执行 mSemaphorePoolThreadHandler.acquire(); 时信号量为0会阻塞线程
     * 执行mSemaphorePoolThreadHandler.release(); 时会释放一个型号量 被阻塞的线程将会被舒爽
     */
    private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
    private Semaphore mSemaphoremPoolThread;

    /**
     * ImageLoader类的构造函数
     *
     * @param ThreadCount 线程池中的线程数
     * @param type        队列调度方式
     */
    private ImageLoader(int ThreadCount, Type type) {
        init(ThreadCount, type);
    }


    /**
     * 单利模式下获取实例方法
     *
     * @return 返回一个ImageLoader实例
     */
    public static ImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader(DEFAULT_THREAD_COUNT, Type.FIFO);
                }
            }
        }
        return mInstance;
    }

    /**
     * 用户自己定制线程数，和执行方式
     * @param threadCount
     * @param type
     * @return
     */
    public static ImageLoader getInstance(int threadCount,Type type) {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader(threadCount, type);
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化操作
     *
     * @param threadCount 线程池中可以存在的线程数
     * @param type        队列调度方式
     */
    private void init(int threadCount, Type type) {

        //后台轮询线程
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //去线程池中去一个线程执行
                        mThreadPool.execute(getTask());
                        try {
                            //请求信号
                            mSemaphoremPoolThread.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                };
                //释放信号量
                mSemaphorePoolThreadHandler.release();
                //开始轮询
                Looper.loop();
            }
        };
        mPoolThread.start();

        //获取程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        //初始化LruCache
        mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //返回bitmap的大小，高度乘以每一行的字节数
                return value.getHeight() * value.getRowBytes();
            }
        };

        //创建线程池
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        //创建任务队列
        mTaskQueue = new LinkedList<Runnable>();
        //指定队列调度方式
        mType = type;

        mSemaphoremPoolThread = new Semaphore(threadCount);


    }

    /**
     * 从任务队列获取一个方法
     *
     * @return
     */
    private Runnable getTask() {
        if (mType == Type.FIFO) {
            return mTaskQueue.removeFirst();
        } else if (mType == Type.LIFO) {
            return mTaskQueue.removeLast();
        }
        return null;
    }

    /**
     * 根据path为imageView设置图片
     *
     * @param path      图片的地址
     * @param imageView 设置图片
     */
    public void loadImage(final String path, final ImageView imageView) {
        imageView.setTag(path);
        if (mUIHandler == null) {
            mUIHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    //获得图片，为imageView回调设置图片
                    ImageBeanHolder imageBean = (ImageBeanHolder) msg.obj;
                    Bitmap bitmap = imageBean.bitmap;
                    ImageView imageView = imageBean.imageView;
                    String path = imageBean.path;
                    //将imageView的tag与imageView的path进行比对防止错乱
                    if (imageView.getTag().toString().equals(path)) {
                        imageView.setImageBitmap(bitmap);
                    }

                }
            };
        }

        //根据path在缓存中找图片
        final Bitmap bitmap = getBitmapFromLruCache(path);

        if (bitmap != null) {
            refreashBitmap(path, imageView, bitmap);
        } else {
            addTask(new Runnable() {
                @Override
                public void run() {
                    //加载图片
                    //获得imageView的宽和高
                    ImageSize imageSize = getImageViewSize(imageView);
                    //图片的压缩
                    Bitmap bitmap = decodeSampleBitmapFromPath(path, imageSize.width, imageSize.height);
                    //将图片加入到缓存中去
                    addBitmapToLruCache(path, bitmap);
                    refreashBitmap(path,imageView,bitmap);
                    //释放信号量
                    mSemaphoremPoolThread.release();
                }
            });
        }

    }

    private void refreashBitmap(String path, ImageView imageView, Bitmap bitmap) {
        Message message = Message.obtain();
        ImageBeanHolder imageBean = new ImageBeanHolder();
        imageBean.bitmap = bitmap;
        imageBean.path = path;
        imageBean.imageView = imageView;
        message.obj = imageBean;
        //将bitmap通过handler发送出去
        mUIHandler.sendMessage(message);
    }

    /**
     * 将压缩后的图片加入到缓存中去
     * @param path path作为key传入
     * @param bitmap 压缩后的图片
     */
    private void addBitmapToLruCache(String path, Bitmap bitmap) {
        if (getBitmapFromLruCache(path) == null) {
            //将图片加入到缓存中去
            mLruCache.put(path, bitmap);
        }
    }

    /**
     * 根据图片的宽高对图片进行压缩
     *
     * @param path   图片地址
     * @param width  压缩后的宽度
     * @param height 压缩后的高度
     * @return 压缩后的图片
     */
    private Bitmap decodeSampleBitmapFromPath(String path, int width, int height) {
        //获取到图片的大小但是并不加载进内存
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = caculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        //再次解析bitmap获得压缩后的图片
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     * 根据图片实际的大小，和imageview的大小计算压缩比例
     *
     * @param options 实际宽和高
     * @param reqWidth   imageView宽
     * @param reqWidth  imageView高
     * @return 比例
     */
    private int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;

        int widthRadio = Math.round(width * 1.0f / reqWidth);
        int heightRadiok = Math.round(height * 1.0f / reqHeight);
        int inSampleSize = Math.max(widthRadio, heightRadiok);
        return inSampleSize;
    }

    /**
     * 根据imageView获得压缩的宽和高
     *
     * @param imageView imageView
     * @return 一个封装了宽高的对象
     */
    private ImageSize getImageViewSize(ImageView imageView) {
        ImageSize imageSize = new ImageSize();
        DisplayMetrics dispaly = imageView.getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        int width = imageView.getWidth();
        if (width <= 0) {
            width = lp.width;
        }
        if (width <= 0) {
            // width = imageView.getMaxWidth();
            //通过反射获取，最大值
            width = getImageViewFieldValue(imageView, "mMaxWidth");
        }
        if (width <= 0) {
            width = dispaly.widthPixels;
        }

        int height = imageView.getHeight();
        if (height <= 0) {
            height = lp.width;
        }
        if (height <= 0) {
            // height = imageView.getMaxHeight();
            //通过反射获取，最大值
            height = getImageViewFieldValue(imageView, "mMaxHeight");
        }
        if (height <= 0) {
            height = dispaly.heightPixels;
        }
        imageSize.width = width;
        imageSize.height = height;
        return imageSize;
    }

    /**
     * 添加task到任务队列，并通知轮询线程取
     * @param runnable
     */
    private synchronized void addTask(Runnable runnable) {
        //将task添加进任务队列
        mTaskQueue.add(runnable);
        //通知handler
        try {
            //如果mPoolThreadHandler为空时说明，前面的初始化他的线程还未执行，使用信号量将这个线程进行阻塞
            if (mPoolThreadHandler == null)
            //获取为0，阻塞这个线程
            mSemaphorePoolThreadHandler.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    /**
     * 根据path从lruCache中获取图片
     *
     * @param path 图片地址
     * @return 一张图片
     */
    private Bitmap getBitmapFromLruCache(String path) {
        return mLruCache.get(path);
    }

    private static int getImageViewFieldValue(Object obj,String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            int fieldValue = field.getInt(obj);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private class ImageBeanHolder {
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }

    private class ImageSize {
        int width;
        int height;
    }
}