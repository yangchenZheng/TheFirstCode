package com.example.zhengyangchen.amnesia.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import com.example.zhengyangchen.amnesia.R;


public class ClearEditText extends EditText implements  
        OnFocusChangeListener, TextWatcher { 
	/**
	 * ɾ����ť������
	 */
    private Drawable mClearDrawable;
    /**
     * ������ť������
     */
    private Drawable mSpeakDrawable;
    /**
     * �ؼ��Ƿ��н���
     */
    private boolean hasFoucs;
    /**
     * �ؼ��Ƿ�������
     */
    private boolean hasContent = false;
    /**
     *����ͼ������¼�
     */
    private onSpeakIconClickListener onSpeakIconClickListener;
 
    public ClearEditText(Context context) { 
    	this(context, null); 
    } 
 
    public ClearEditText(Context context, AttributeSet attrs) { 
    	//���ﹹ�췽��Ҳ����Ҫ����������ܶ����Բ�����XML���涨��
    	this(context, attrs, android.R.attr.editTextStyle); 
    } 
    
    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    
    private void init() { 
    	//��ȡEditText��DrawableRight,����û���������Ǿ�ʹ��Ĭ�ϵ�ͼƬ
    	mClearDrawable = getCompoundDrawables()[2];
        mSpeakDrawable = getResources().getDrawable(R.drawable.ic_btn_speak_now);
        if (mClearDrawable == null) { 
//        	throw new NullPointerException("You can add drawableRight attribute in XML");
        	mClearDrawable = getResources().getDrawable(R.drawable.ic_suggestions_delete);
        }
        mSpeakDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight()); 
        //Ĭ����������ͼ��
        setClearIconVisible(false); 
        //���ý���ı�ļ���
        setOnFocusChangeListener(this); 
        //����������������ݷ����ı�ļ���
        addTextChangedListener(this); 
    } 
 
 
    /**
     * ��Ϊ���ǲ���ֱ�Ӹ�EditText���õ���¼������������ü�ס���ǰ��µ�λ����ģ�����¼�
     * �����ǰ��µ�λ�� ��  EditText�Ŀ�� - ͼ�굽�ؼ��ұߵļ�� - ͼ��Ŀ��  ��
     * EditText�Ŀ�� - ͼ�굽�ؼ��ұߵļ��֮�����Ǿ�������ͼ�꣬��ֱ�����û�п���
     */
    @Override 
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (getCompoundDrawables()[2] != null) {

				boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
						&& (event.getX() < ((getWidth() - getPaddingRight())));

                if (touchable && hasContent) {
                    this.setText("");
                    setClearIconVisible(false);
                    hasContent = false;
                } else if (touchable && !hasContent) {
                    /*this.clearFocus();
                   InputMethodManager inputMethodManager = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(this.getWindowToken(),0);*/
                    onSpeakIconClickListener.onClick();
                }
            }
		}

		return super.onTouchEvent(event);
	}
 
    /**
     * ��ClearEditText���㷢���仯��ʱ���ж������ַ��������������ͼ�����ʾ������
     */
    @Override 
    public void onFocusChange(View v, boolean hasFocus) { 
    	this.hasFoucs = hasFocus;
        if (hasFocus) { 
            setClearIconVisible(getText().length() > 0); 
        } else { 
            setClearIconVisible(false); 
        } 
    } 
 
 
    /**
     * �������ͼ�����ʾ�����أ�����setCompoundDrawablesΪEditText������ȥ
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) { 
        Drawable right = visible ? mClearDrawable : mSpeakDrawable;
        setCompoundDrawables(getCompoundDrawables()[0], 
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]); 
    } 
    
    
    /**
     * ��������������ݷ����仯��ʱ��ص��ķ���
     */
    @Override 
    public void onTextChanged(CharSequence s, int start, int count, 
            int after) {
            	if(hasFoucs) {
                    if (s.length() > 0) {
                        setClearIconVisible(s.length() > 0);
                        hasContent = true;
                    } else {
                        hasContent = false;
                        setClearIconVisible(false);
                    }
                }
    }
 
    @Override 
    public void beforeTextChanged(CharSequence s, int start, int count, 
            int after) { 
         
    } 
 
    @Override 
    public void afterTextChanged(Editable s) { 
         
    } 
    
   
    /**
     * ���ûζ�����
     */
    public void setShakeAnimation(){
    	this.setAnimation(shakeAnimation(5));
    }
    
    
    /**
     * �ζ�����
     * @param counts 1���ӻζ�������
     * @return
     */
    public static Animation shakeAnimation(int counts){
    	Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
    	translateAnimation.setInterpolator(new CycleInterpolator(counts));
    	translateAnimation.setDuration(1000);
    	return translateAnimation;
    }

    /**
     * �ӿڻص�
     * @param onSpeakIconClickListener
     */
    public void setOnSpeakIconClickListener(
            onSpeakIconClickListener onSpeakIconClickListener) {
    this.onSpeakIconClickListener = onSpeakIconClickListener;
    }
    /**
     * �������ͼ��Ľӿ�
     */
    public interface onSpeakIconClickListener {
        void onClick();
    }
 
 
}
