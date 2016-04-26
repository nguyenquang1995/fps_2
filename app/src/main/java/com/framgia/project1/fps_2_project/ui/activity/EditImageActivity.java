package com.framgia.project1.fps_2_project.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import com.framgia.project1.fps_2_project.R;
import com.framgia.project1.fps_2_project.ui.widget.GLToolbox;
import com.framgia.project1.fps_2_project.ui.widget.TextureRenderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class EditImageActivity extends AppCompatActivity implements GLSurfaceView.Renderer,
    View.OnClickListener{
    private GLSurfaceView mEffectView;
    private int[] mTextures = new int[2];
    private EffectContext mEffectContext;
    private Effect mEffect;
    private TextureRenderer mTexRenderer = new TextureRenderer();
    private int mImageWidth;
    private int mImageHeight;
    private boolean mInitialized = false;
    private int mCurrentEffect;
    public void setCurrentEffect(int effect) {
        mCurrentEffect = effect;
    }
    Button mButNone, mButAutofix,mButbw, mButbrightness, mButcontrast, mButcrossprocess,
        mButdocumentary, mButduotone, mButfilllight,mButfisheye, mButflipvert, mButfliphor,
        mButgrain, mButlomoish, mButnegative, mButposterize, mButrotate, mButsaturate, mButsepia,
        mButGray, mButsharpen, mButtemperature, mButtint, mButvignette;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        mEffectView = (GLSurfaceView) findViewById(R.id.effectsview);
        mEffectView.setEGLContextClientVersion(2);
        mEffectView.setRenderer(this);
        mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mCurrentEffect = R.id.none;
        initButton();
    }

    private void initButton() {
        mButNone=(Button)findViewById(R.id.none);
        mButNone.setOnClickListener(this);
        mButAutofix=(Button)findViewById(R.id.autofix);
        mButAutofix.setOnClickListener(this);
        mButbw=(Button)findViewById(R.id.bw);
        mButbw.setOnClickListener(this);
        mButbrightness=(Button)findViewById(R.id.brightness);
        mButbrightness.setOnClickListener(this);
        mButcontrast=(Button)findViewById(R.id.contrast);
        mButcontrast.setOnClickListener(this);
        mButcrossprocess=(Button)findViewById(R.id.crossprocess);
        mButcrossprocess.setOnClickListener(this);
        mButdocumentary=(Button)findViewById(R.id.documentary);
        mButdocumentary.setOnClickListener(this);
        mButduotone=(Button)findViewById(R.id.duotone);
        mButduotone.setOnClickListener(this);
        mButfilllight=(Button)findViewById(R.id.filllight);
        mButfilllight.setOnClickListener(this);
        mButfisheye=(Button)findViewById(R.id.fisheye);
        mButfisheye.setOnClickListener(this);
        mButflipvert=(Button)findViewById(R.id.flipvert);
        mButflipvert.setOnClickListener(this);
        mButfliphor=(Button)findViewById(R.id.fliphor);
        mButfliphor.setOnClickListener(this);
        mButgrain=(Button)findViewById(R.id.grain);
        mButgrain.setOnClickListener(this);
        mButlomoish=(Button)findViewById(R.id.lomoish);
        mButlomoish.setOnClickListener(this);
        mButnegative=(Button)findViewById(R.id.negative);
        mButnegative.setOnClickListener(this);
        mButposterize=(Button)findViewById(R.id.posterize);
        mButposterize.setOnClickListener(this);
        mButrotate=(Button)findViewById(R.id.rotate);
        mButrotate.setOnClickListener(this);
        mButsaturate=(Button)findViewById(R.id.saturate);
        mButsaturate.setOnClickListener(this);
        mButsepia=(Button)findViewById(R.id.sepia);
        mButsepia.setOnClickListener(this);
        mButGray=(Button)findViewById(R.id.grayscale);
        mButGray.setOnClickListener(this);
        mButsharpen=(Button)findViewById(R.id.sharpen);
        mButsharpen.setOnClickListener(this);
        mButtemperature=(Button)findViewById(R.id.temperature);
        mButtemperature.setOnClickListener(this);
        mButtint=(Button)findViewById(R.id.tint);
        mButtint.setOnClickListener(this);
        mButvignette=(Button)findViewById(R.id.vignette);
        mButvignette.setOnClickListener(this);
    }

    private void loadTextures() {
        GLES20.glGenTextures(2, mTextures, 0);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
            R.drawable.usb_android);
        mImageWidth = bitmap.getWidth();
        mImageHeight = bitmap.getHeight();
        mTexRenderer.updateTextureSize(mImageWidth, mImageHeight);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLToolbox.initTexParams();
    }
    private void initEffect() {
        EffectFactory effectFactory = mEffectContext.getFactory();
        if (mEffect != null) {
            mEffect.release();
        }
        switch (mCurrentEffect) {
            case R.id.none:
                break;
            case R.id.autofix:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_AUTOFIX);
                mEffect.setParameter(getString(R.string.scale), 0.5f);
                break;
            case R.id.bw:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_BLACKWHITE);
                mEffect.setParameter(getString(R.string.black), .1f);
                mEffect.setParameter(getString(R.string.white), .7f);
                break;
            case R.id.brightness:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_BRIGHTNESS);
                mEffect.setParameter(getString(R.string.brightness), 2.0f);
                break;
            case R.id.contrast:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_CONTRAST);
                mEffect.setParameter(getString(R.string.contrast), 1.4f);
                break;
            case R.id.crossprocess:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_CROSSPROCESS);
                break;
            case R.id.documentary:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_DOCUMENTARY);
                break;
            case R.id.duotone:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_DUOTONE);
                mEffect.setParameter(getString(R.string.first_color), Color.YELLOW);
                mEffect.setParameter(getString(R.string.second_color), Color.DKGRAY);
                break;
            case R.id.filllight:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_FILLLIGHT);
                mEffect.setParameter(getString(R.string.strength), .8f);
                break;
            case R.id.fisheye:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_FISHEYE);
                mEffect.setParameter(getString(R.string.scale), .5f);
                break;
            case R.id.flipvert:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_FLIP);
                mEffect.setParameter(getString(R.string.vertical), true);
                break;
            case R.id.fliphor:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_FLIP);
                mEffect.setParameter(getString(R.string.horizontal), true);
                break;
            case R.id.grain:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_GRAIN);
                mEffect.setParameter(getString(R.string.strength), 1.0f);
                break;
            case R.id.grayscale:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_GRAYSCALE);
                break;
            case R.id.lomoish:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_LOMOISH);
                break;
            case R.id.negative:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_NEGATIVE);
                break;
            case R.id.posterize:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_POSTERIZE);
                break;
            case R.id.rotate:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_ROTATE);
                mEffect.setParameter(getString(R.string.angle), 180);
                break;
            case R.id.saturate:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_SATURATE);
                mEffect.setParameter(getString(R.string.scale), .5f);
                break;
            case R.id.sepia:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_SEPIA);
                break;
            case R.id.sharpen:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_SHARPEN);
                break;
            case R.id.temperature:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_TEMPERATURE);
                mEffect.setParameter(getString(R.string.scale), .9f);
                break;
            case R.id.tint:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_TINT);
                mEffect.setParameter(getString(R.string.tint), Color.MAGENTA);
                break;
            case R.id.vignette:
                mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_VIGNETTE);
                mEffect.setParameter(getString(R.string.scale), .5f);
                break;
            default:
                break;
        }
    }
    private void applyEffect() {
        mEffect.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);
    }
    private void renderResult() {
        if (mCurrentEffect != R.id.none) {
            mTexRenderer.renderTexture(mTextures[1]);
        }
        else {
            mTexRenderer.renderTexture(mTextures[0]);
        }
    }
    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mInitialized) {
            mEffectContext = EffectContext.createWithCurrentGlContext();
            mTexRenderer.init();
            loadTextures();
            mInitialized = true;
        }
        if (mCurrentEffect != R.id.none) {
            initEffect();
            applyEffect();
        }
        renderResult();
    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mTexRenderer != null) {
            mTexRenderer.updateViewSize(width, height);
        }
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    @Override
    public void onClick(View v) {
        setCurrentEffect(v.getId());
        mEffectView.requestRender();
    }
}