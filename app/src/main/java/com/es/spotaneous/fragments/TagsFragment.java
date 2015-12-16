package com.es.spotaneous.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.es.spotaneous.R;
import com.es.spotaneous.utils.SharedConfigs;

import java.util.ArrayList;

import me.kaede.tagview.OnTagClickListener;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

public class TagsFragment extends Fragment {

    private ArrayList<String> tagsList;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        tagsList = new ArrayList<>();
        context = getActivity();
        String[] testArray = getResources().getStringArray(R.array.interesses);
        final TagView tagView = new TagView(getActivity());

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(20, 0, 20, 0);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        for (String t : testArray) {
            Tag tag = new Tag(t);
            tag.tagTextColor = Color.WHITE;
            tag.layoutColor =  getResources().getColor(R.color.logo_color_2);
            tag.layoutBorderColor =  getResources().getColor(R.color.logo_color_2);
            tag.layoutColorPress =  getResources().getColor(R.color.logo_color_1);
            tag.isDeletable = false;
            tagView.addTag(tag);
        }

        tagView.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                if(tag.layoutColor == getResources().getColor(R.color.logo_color_1)){
                    tagView.remove(position);
                    tag.layoutColor = getResources().getColor(R.color.logo_color_2);
                    tag.layoutBorderColor =  getResources().getColor(R.color.logo_color_2);
                    tagView.addTagPosition(tag, position);
                    unsubscriveTag(tag);
                }else{
                    tagView.remove(position);
                    tag.layoutColor = getResources().getColor(R.color.logo_color_1);
                    tag.layoutBorderColor =  Color.WHITE;
                    tagView.addTagPosition(tag, position);
                    subscriveTag(tag);
                }

            }
        });

        tagView.setGravity(RelativeLayout.CENTER_HORIZONTAL);
        linearLayout.addView(tagView);
        return linearLayout;
    }

    private void subscriveTag(Tag tag){
        tagsList.add(tag.text);
        SharedConfigs.addList("listtags", tagsList, context);
    }

    private void unsubscriveTag(Tag tag){
        tagsList.remove(tag.text);
        SharedConfigs.addList("listtags", tagsList, context);
    }

    @Override
    public void onSaveInstanceState( Bundle outState ) {

    }

    @Override
    public void onDetach() {
        SharedConfigs.addList("listtags",tagsList,context);
        super.onDetach();
    }
}
