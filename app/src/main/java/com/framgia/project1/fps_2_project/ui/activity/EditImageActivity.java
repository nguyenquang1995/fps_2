package com.framgia.project1.fps_2_project.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.framgia.project1.fps_2_project.R;
import com.framgia.project1.fps_2_project.ui.widget.GLToolbox;
import com.framgia.project1.fps_2_project.ui.widget.TextureRenderer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class EditImageActivity extends AppCompatActivity implements GLSurfaceView.Renderer,
    View.OnClickListener {
    public static Bitmap sBitmap;
    protected boolean checkSave = false;
    Button mButNone, mButAutofix, mButbw, mButbrightness, mButcontrast, mButcrossprocess,
        mButdocumentary, mButduotone, mButfilllight, mButfisheye, mButflipvert, mButfliphor,
        mButgrain, mButlomoish, mButnegative, mButposterize, mButrotate, mButsaturate, mButsepia,
        mButGray, mButsharpen, mButtemperature, mButtint, mButvignette, mBtnSave, mBtnCancel;
    Dialog dialog;
    private GLSurfaceView mEffectView;
    private int[] mTextures = new int[2];
    private EffectContext mEffectContext;
    private Effect mEffect;
    private TextureRenderer mTexRenderer = new TextureRenderer();
    private int mImageWidth;
    private int mImageHeight;
    private int mViewWidth;
    private int mViewHeght;
    private boolean mInitialized = false;
    private String mImageName = "";
    private int mCurrentEffect;

    public void setCurrentEffect(int effect) {
        mCurrentEffect = effect;
    }

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
        mButNone = (Button) findViewById(R.id.none);
        mButNone.setOnClickListener(this);
        mButAutofix = (Button) findViewById(R.id.autofix);
        mButAutofix.setOnClickListener(this);
        mButbw = (Button) findViewById(R.id.bw);
        mButbw.setOnClickListener(this);
        mButbrightness = (Button) findViewById(R.id.brightness);
        mButbrightness.setOnClickListener(this);
        mButcontrast = (Button) findViewById(R.id.contrast);
        mButcontrast.setOnClickListener(this);
        mButcrossprocess = (Button) findViewById(R.id.crossprocess);
        mButcrossprocess.setOnClickListener(this);
        mButdocumentary = (Button) findViewById(R.id.documentary);
        mButdocumentary.setOnClickListener(this);
        mButduotone = (Button) findViewById(R.id.duotone);
        mButduotone.setOnClickListener(this);
        mButfilllight = (Button) findViewById(R.id.filllight);
        mButfilllight.setOnClickListener(this);
        mButfisheye = (Button) findViewById(R.id.fisheye);
        mButfisheye.setOnClickListener(this);
        mButflipvert = (Button) findViewById(R.id.flipvert);
        mButflipvert.setOnClickListener(this);
        mButfliphor = (Button) findViewById(R.id.fliphor);
        mButfliphor.setOnClickListener(this);
        mButgrain = (Button) findViewById(R.id.grain);
        mButgrain.setOnClickListener(this);
        mButlomoish = (Button) findViewById(R.id.lomoish);
        mButlomoish.setOnClickListener(this);
        mButnegative = (Button) findViewById(R.id.negative);
        mButnegative.setOnClickListener(this);
        mButposterize = (Button) findViewById(R.id.posterize);
        mButposterize.setOnClickListener(this);
        mButrotate = (Button) findViewById(R.id.rotate);
        mButrotate.setOnClickListener(this);
        mButsaturate = (Button) findViewById(R.id.saturate);
        mButsaturate.setOnClickListener(this);
        mButsepia = (Button) findViewById(R.id.sepia);
        mButsepia.setOnClickListener(this);
        mButGray = (Button) findViewById(R.id.grayscale);
        mButGray.setOnClickListener(this);
        mButsharpen = (Button) findViewById(R.id.sharpen);
        mButsharpen.setOnClickListener(this);
        mButtemperature = (Button) findViewById(R.id.temperature);
        mButtemperature.setOnClickListener(this);
        mButtint = (Button) findViewById(R.id.tint);
        mButtint.setOnClickListener(this);
        mButvignette = (Button) findViewById(R.id.vignette);
        mButvignette.setOnClickListener(this);
    }

    private void setDialog() {
        dialog = new Dialog(EditImageActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle(R.string.save_image_title);
        final EditText edt = (EditText) dialog.findViewById(R.id.edtSave);
        mImageName = edt.getText().toString();
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mImageName = edt.getText().toString();
                if (mImageName.length() > 0)
                    mBtnSave.setEnabled(true);
                else
                    mBtnSave.setEnabled(false);
            }
        });
        dialog.show();
    }

    private Bitmap createBitmapFromGLSurface(int x, int y, int w, int h, GL10 gl)
        throws OutOfMemoryError {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);
        try {
            gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            return null;
        }
        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
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
        mViewWidth = mEffectView.getWidth();
        mViewHeght = mEffectView.getHeight();
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
        } else {
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
        if (checkSave == true) {
            saveBitmapToFile(createBitmapFromGLSurface(0, 0, mViewWidth, mViewHeght, gl),
                mImageName);
        }
    }

    private void saveBitmapToFile(Bitmap bitmap, String name) {
        OutputStream out = null;
        try {
            File file =
                new File(Environment.getExternalStorageDirectory() + "/" + name + getString(R
                    .string.png));
            out = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
            this.sendBroadcast(intent);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
        switch (v.getId()) {
            case R.id.btnSave:
                setDialog();
                break;
            case R.id.save:
                checkSave = true;
                mEffectView.requestRender();
                dialog.dismiss();
                Toast.makeText(EditImageActivity.this, R.string.notice_save, Toast
                    .LENGTH_LONG).show();
                break;
            case R.id.cancel:
                dialog.dismiss();
                break;
            default:
                setCurrentEffect(v.getId());
                mEffectView.requestRender();
                break;
        }
    }
}