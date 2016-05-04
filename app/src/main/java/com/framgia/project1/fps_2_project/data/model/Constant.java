package com.framgia.project1.fps_2_project.data.model;

/**
 * Created by nguyenxuantung on 20/04/2016.
 */
public interface Constant {
    public static final String GL_ATTACH_SHADER="glAttachShader";
    public static final String NOTICE_PROGRAM="Could not link program: ";
    public static final String NOTICE_SHADER="Could not compile shader ";
    public static final String VERTEX_SHADER="attribute vec4 a_position;\n" +
        "attribute vec2 a_texcoord;\n" +
        "varying vec2 v_texcoord;\n" +
        "void main() {\n" +
        "  gl_Position = a_position;\n" +
        "  v_texcoord = a_texcoord;\n" +
        "}\n";
    public static final String FRAGMENT_SHADER="precision mediump float;\n" +
        "uniform sampler2D tex_sampler;\n" +
        "varying vec2 v_texcoord;\n" +
        "void main() {\n" +
        "  gl_FragColor = texture2D(tex_sampler, v_texcoord);\n" +
        "}\n";
    public static final String TEX_SAMPLER="tex_sampler";
    public static final String TEXCOORD="a_texcoord";
    public static final String POSITION="a_position";
    public static final String USE_PROGRAM="glUseProgram";
    public static final String VIEW_PORT="glViewport";
    public static final String SETUP_VERTEX="vertex attribute setup";
    public static final String ACTIVE_TEXTURE="glActiveTexture";
    public static final String BIND_TEXTURE="glBindTexture";
    public static final String TABLE_IMAGE= "tblImage";
    public static final String TABLE_VIDEO= "tblVideo";
    //columns of tblImage
    public static final String COLUMN_ID= "id";
    public static final String COLUMN_IMAGE_NAME= "image_name";
    public static final String COLUMN_IMAGE_PATH= "image_path";
    public static final String COLUMN_IMAGE_LIKE= "image_like";
    //columns of tblVideo
    public static final String COLUMN_VIDEO_NAME = "video_name";
    public static final String COLUMN_VIDEO_PATH = "video_path";
    public static final String[] LIST_ITEM_FACEBOOK= { "Take Photo", "Choose from Library","Cancel" };
}