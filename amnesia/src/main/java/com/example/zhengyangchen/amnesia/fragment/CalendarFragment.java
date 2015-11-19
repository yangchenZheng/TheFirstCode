package com.example.zhengyangchen.amnesia.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;
import com.example.zhengyangchen.amnesia.R;
import com.example.zhengyangchen.amnesia.adapter.MemoAdapter;
import com.example.zhengyangchen.amnesia.bean.Memo;
import com.example.zhengyangchen.amnesia.dao.MemoDB;
import com.example.zhengyangchen.amnesia.util.Util;

import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by zhengyangchen on 2015/10/22.
 */
public class CalendarFragment extends Fragment {
    /**
     * 碎片的view
     */
    private View mView;
    /**
     * cardListView 用来显示内容的
     */
    private MaterialListView mMaterialListView;
    /**
     * 用于存储遍历出来的数据
     */
    private List<Memo> mMemoList;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.calendar_fragment, container, false);
        mContext = getContext();
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initView();
    }


    private void initData() {
        //将数据从数据库遍历出来
        mMemoList = MemoDB.getInstance(getContext()).loadMemo();
    }

    private void initView() {
        mMaterialListView = (MaterialListView) mView.findViewById(R.id.id_card_lv);
        mMaterialListView.setItemAnimator(new SlideInLeftAnimator());
        mMaterialListView.getItemAnimator().setAddDuration(300);
        mMaterialListView.getItemAnimator().setRemoveDuration(300);
        for (int i = 0; i < mMemoList.size(); i++) {
            final Memo memo = mMemoList.get(i);
            Card card;
            if (TextUtils.isEmpty(memo.getPhotoUrl())) {
                card = new Card.Builder(mContext)
                        .setTag(memo)
                        .setDismissible()
                        .withProvider(new CardProvider())
                        .setTitle(memo.getMemoDesc())
                        .setDescription(MemoAdapter.dateLongToString(memo.getDateStr()))
                        .setLayout(R.layout.material_basic_buttons_card)
                        .addAction(R.id.left_text_button, new TextViewAction(mContext)
                                .setText("已完成")
                                .setTextResourceColor(R.color.colorPrimary)
                                .setListener(new OnActionClickListener() {
                                    @Override
                                    public void onActionClicked(View view, Card card) {
                                        Memo memo1 = (Memo) card.getTag();
                                        assert memo1 != null;
                                        MemoDB.deleteMemoById(memo1.getId(), mContext);
                                        card.dismiss();
                                    }
                                }))
                        .endConfig()
                        .build();
            } else {
                String[] paths = memo.getPhotoUrl().split("|");

                card = new Card.Builder(mContext)
                        .setTag(memo)
                        .setDismissible()
                        .withProvider(new CardProvider())
                        .setTitle(memo.getMemoDesc())
                        .setDescription(MemoAdapter.dateLongToString(memo.getDateStr()))
                        .setLayout(R.layout.material_basic_image_buttons_card_layout)
                        .setDrawable(R.drawable.ic_launcher)
                        .addAction(R.id.image, new TextViewAction(mContext)
                                .setListener(new OnActionClickListener() {
                                    @Override
                                    public void onActionClicked(View view, Card card) {
                                        Memo memo1 = (Memo) card.getTag();
                                        Util.showShortToast(mContext, memo.getMemoDesc() + "!");
                                    }
                                }))
                        .addAction(R.id.left_text_button, new TextViewAction(mContext)
                                .setText("已完成")
                                .setTextResourceColor(R.color.colorPrimary)
                                .setListener(new OnActionClickListener() {
                                    @Override
                                    public void onActionClicked(View view, Card card) {
                                        Memo memo1 = (Memo) card.getTag();
                                        assert memo1 != null;
                                        MemoDB.deleteMemoById(memo1.getId(), mContext);
                                        card.dismiss();
                                    }
                                }))
                        .endConfig()
                        .build();
            }

            mMaterialListView.getAdapter().add(card);

        }
    }

}
