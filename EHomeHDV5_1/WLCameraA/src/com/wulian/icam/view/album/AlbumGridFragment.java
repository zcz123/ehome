/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wulian.icam.view.album;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.model.AlbumEntity;
import com.wulian.icam.view.album.utils.ImageCache;
import com.wulian.icam.view.album.utils.ImageResizer;
import com.wulian.icam.view.album.utils.RecyclingImageView;
import com.wulian.icam.view.album.utils.Utils;

/**
 * The main fragment that powers the ImageGridActivity screen. Fairly straight
 * forward GridView implementation with the key addition being the ImageWorker
 * class w/ImageCache to load children asynchronously, keeping the UI nice and
 * smooth and caching thumbnails for quick retrieval. The cache is retained over
 * configuration changes like orientation change so the images are populated
 * quickly if, for example, the user rotates the device.
 */
public class AlbumGridFragment extends Fragment implements
        AdapterView.OnItemClickListener {
    private static final String TAG = "AlbumGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter;
    private ImageResizer mImageResizer;
    private String[] mSnapShot;
    private AlbumEntity mAlbumEntity;

    /**
     * Empty constructor as per the Fragment documentation
     */
    public AlbumGridFragment(AlbumEntity ae) {
        mAlbumEntity = ae;
        loadJpgs(ae);
    }

    /**
     * @param ae
     * @Function 加载所有jpg图片
     * @author Wangjj
     * @date 2015年6月23日
     */

    public void loadJpgs(AlbumEntity ae) {
        File[] jpgImgs = new File(ae.getPath()).listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase(Locale.ENGLISH).endsWith(".jpg");
            }
        });

        // 对图片排序
        Arrays.sort(jpgImgs, new Comparator<File>() {

            @Override
            public int compare(File lhs, File rhs) {
                if (lhs.lastModified() > rhs.lastModified())
                    return -1;// 较新的靠前排
                else if (lhs.lastModified() < rhs.lastModified()) {
                    return 1;
                }
                return 0;
            }
        });
        mSnapShot = new String[jpgImgs.length];
        for (int i = 0; i < jpgImgs.length; i++) {
            mSnapShot[i] = jpgImgs[i].getAbsolutePath();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageThumbSize = getResources().getDimensionPixelSize(
                R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(
                R.dimen.image_thumbnail_spacing);

        mAdapter = new ImageAdapter(getActivity());

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(
                getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of
        // app memory

        // The ImageFetcher takes care of loading images into our ImageView
        // children asynchronously
        mImageResizer = new ImageResizer(getActivity(), mImageThumbSize);
        mImageResizer.setLoadingImage(R.drawable.empty_photo);
        mImageResizer.addImageCache(getActivity().getSupportFragmentManager(),
                cacheParams);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_album_grid,
                container, false);
        (v.findViewById(R.id.titlebar_back))
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        AlbumGridFragment.this.getActivity().finish();
                    }
                });
        ((TextView) v.findViewById(R.id.titlebar_title)).setText(mAlbumEntity
                .getDeviceName());
        final GridView mGridView = (GridView) v.findViewById(R.id.gridView);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView,
                                             int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help
                    // with performance
                    if (!Utils.hasHoneycomb()) {
                        mImageResizer.setPauseWork(true);
                    }
                } else {
                    mImageResizer.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        // This listener is used to get the final width of the GridView and then
        // calculate the
        // number of columns and the width of each column. The width of each
        // column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used
        // to set the height
        // of each view so we get nice square thumbnails.
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        if (mAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(mGridView
                                    .getWidth()
                                    / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth = (mGridView.getWidth() / numColumns)
                                        - mImageThumbSpacing;
                                mAdapter.setNumColumns(numColumns);
                                mAdapter.setItemHeight(columnWidth);
                                if (Utils.hasJellyBean()) {
                                    mGridView.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                    mGridView.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }
                            }
                        }
                    }
                });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageResizer.setExitTasksEarly(false);
        loadJpgs(mAlbumEntity);
        mAdapter.notifyDataSetChanged();
        if (mSnapShot.length == 0) {
//            AlbumGridFragment.this.getActivity().finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageResizer.setPauseWork(false);
        mImageResizer.setExitTasksEarly(true);
        mImageResizer.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageResizer.closeCache();
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        final Intent i = new Intent(getActivity(), AlbumPicActivity.class);
        i.putExtra("AlbumEntity", mAlbumEntity);
        i.putExtra("position", position);
        if (Utils.hasJellyBean()) {
            // makeThumbnailScaleUpAnimation() looks kind of ugly here as the
            // loading spinner may
            // show plus the thumbnail image in GridView is cropped. so using
            // makeScaleUpAnimation() instead.
            ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v,
                    0, 0, v.getWidth(), v.getHeight());
            getActivity().startActivity(i, options.toBundle());
        } else {
            startActivity(i);
        }
    }

    /**
     * The main adapter that backs the GridView. This is fairly standard except
     * the number of columns in the GridView is used to create a fake top row of
     * empty views as we use a transparent ActionBar and don't want the real top
     * row of images to start off covered by it.
     */
    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;// 多少列

        private GridView.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        }

        @Override
        public int getCount() {
            // If columns have yet to be determined, return no items
            if (getNumColumns() == 0) {
                return 0;
            }

            // Size + number of columns for top empty row
            return mSnapShot.length;
        }

        @Override
        public Object getItem(int position) {
            return mSnapShot[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {

            // Now handle the main ImageView thumbnails
            ImageView imageView;
            if (convertView == null) { // if it's not recycled, instantiate and
                // initialize
                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);
            } else { // Otherwise re-use the converted view
                imageView = (ImageView) convertView;
            }

            // Check the height matches our calculated column width
            if (imageView.getLayoutParams().height != mItemHeight) {
                imageView.setLayoutParams(mImageViewLayoutParams);
            }

            // Finally load the image asynchronously into the ImageView, this
            // also takes care of
            // setting a placeholder image while the background thread runs
            mImageResizer.loadImage(mSnapShot[position], imageView);
            return imageView;

        }

        /**
         * Sets the item height. Useful for when we know the column width so the
         * height can be set to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    LayoutParams.MATCH_PARENT, mItemHeight);
            mImageResizer.setImageSize(height);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }
    }
}
