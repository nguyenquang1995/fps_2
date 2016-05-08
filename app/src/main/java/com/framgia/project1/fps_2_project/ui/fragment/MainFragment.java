package com.framgia.project1.fps_2_project.ui.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.framgia.project1.fps_2_project.R;
import com.framgia.project1.fps_2_project.ui.activity.CropImageActivity;
import com.framgia.project1.fps_2_project.ui.activity.EditImageActivity;
import com.framgia.project1.fps_2_project.ui.widget.CropDemoPreset;
import com.framgia.project1.fps_2_project.ui.widget.CropImageViewOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

/**
 * The fragment that will show the Image Cropping UI by requested preset.
 */
public final class MainFragment extends Fragment
    implements CropImageView.OnSetImageUriCompleteListener,
    CropImageView.OnGetCroppedImageCompleteListener {
    //region: Fields and Consts
    private CropDemoPreset mDemoPreset;
    private CropImageView mCropImageView;
    //endregion

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static MainFragment newInstance(CropDemoPreset demoPreset, Context context) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(context.getString(R.string.demo_preset), demoPreset.name());
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Set the image to show for cropping.
     */
    public void setImageUri(Uri imageUri) {
        mCropImageView.setImageUriAsync(imageUri);
    }

    /**
     * Set the options of the crop image view to the given values.
     */
    public void setCropImageViewOptions(CropImageViewOptions options) {
        mCropImageView.setScaleType(options.scaleType);
        mCropImageView.setCropShape(options.cropShape);
        mCropImageView.setGuidelines(options.guidelines);
        mCropImageView.setAspectRatio(options.aspectRatio.first, options.aspectRatio.second);
        mCropImageView.setFixedAspectRatio(options.fixAspectRatio);
        mCropImageView.setShowCropOverlay(options.showCropOverlay);
        mCropImageView.setShowProgressBar(options.showProgressBar);
        mCropImageView.setAutoZoomEnabled(options.autoZoomEnabled);
        mCropImageView.setMaxZoom(options.maxZoomLevel);
    }

    /**
     * Set the initial rectangle to use.
     */
    public void setInitialCropRect() {
        mCropImageView.setCropRect(new Rect(100, 300, 500, 1200));
    }

    /**
     * Reset crop window to initial rectangle.
     */
    public void resetCropRect() {
        mCropImageView.resetCropRect();
    }

    public void updateCurrentCropViewOptions() {
        CropImageViewOptions options = new CropImageViewOptions();
        options.scaleType = mCropImageView.getScaleType();
        options.cropShape = mCropImageView.getCropShape();
        options.guidelines = mCropImageView.getGuidelines();
        options.aspectRatio = mCropImageView.getAspectRatio();
        options.fixAspectRatio = mCropImageView.isFixAspectRatio();
        options.showCropOverlay = mCropImageView.isShowCropOverlay();
        options.showProgressBar = mCropImageView.isShowProgressBar();
        options.autoZoomEnabled = mCropImageView.isAutoZoomEnabled();
        options.maxZoomLevel = mCropImageView.getMaxZoom();
        ((CropImageActivity) getActivity()).setCurrentOptions(options);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        switch (mDemoPreset) {
            case RECT:
                rootView = inflater.inflate(R.layout.fragment_main_rect, container, false);
                break;
            case CIRCULAR:
                rootView = inflater.inflate(R.layout.fragment_main_oval, container, false);
                break;
            case CUSTOMIZED_OVERLAY:
                rootView = inflater.inflate(R.layout.fragment_main_customized, container, false);
                break;
            case MIN_MAX_OVERRIDE:
                rootView = inflater.inflate(R.layout.fragment_main_min_max, container, false);
                break;
            case SCALE_CENTER_INSIDE:
                rootView = inflater.inflate(R.layout.fragment_main_scale_center, container, false);
                break;
            case CUSTOM:
                rootView = inflater.inflate(R.layout.fragment_main_rect, container, false);
                break;
            default:
                throw new IllegalStateException(getString(R.string.unknown_preset) + mDemoPreset);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCropImageView = (CropImageView) view.findViewById(R.id.cropImageView);
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnGetCroppedImageCompleteListener(this);
        updateCurrentCropViewOptions();
        if (savedInstanceState == null) {
            if (mDemoPreset == CropDemoPreset.SCALE_CENTER_INSIDE) {
                mCropImageView.setImageResource(R.drawable.usb_android);
            } else {
                mCropImageView.setImageResource(R.drawable.usb_android);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_action_crop) {
            mCropImageView.getCroppedImageAsync();
            return true;
        } else if (item.getItemId() == R.id.main_action_rotate) {
            mCropImageView.rotateImage(90);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mDemoPreset =
            CropDemoPreset.valueOf(getArguments().getString(getString(R.string.demo_preset)));
        ((CropImageActivity) activity).setCurrentFragment(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCropImageView != null) {
            mCropImageView.setOnSetImageUriCompleteListener(null);
            mCropImageView.setOnGetCroppedImageCompleteListener(null);
        }
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            Toast.makeText(getActivity(), getString(R.string.load_successful), Toast.LENGTH_SHORT)
                .show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.load_fail) + error.getMessage(),
                Toast.LENGTH_LONG).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onGetCroppedImageComplete(CropImageView view, Bitmap bitmap, Exception error) {
        if (error == null) {
            EditImageActivity.sBitmap =
                mCropImageView.getCropShape() == CropImageView.CropShape.OVAL
                    ? CropImage.toOvalBitmap(bitmap)
                    : bitmap;
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), getString(R.string.crop_failed) + error.getMessage(),
                Toast.LENGTH_LONG).show();
        }
    }
}