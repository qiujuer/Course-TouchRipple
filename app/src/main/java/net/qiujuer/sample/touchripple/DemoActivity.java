package net.qiujuer.sample.touchripple;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import net.qiujuer.genius.ui.drawable.TouchEffectDrawable;
import net.qiujuer.genius.ui.drawable.effect.EffectFactory;
import net.qiujuer.genius.ui.widget.Button;

public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        View view = findViewById(R.id.lay_shader);
        final TouchEffectDrawable drawable = new TouchEffectDrawable();
        view.setBackgroundDrawable(drawable);
        drawable.getPaint().setColor(getResources().getColor(R.color.grey_50));
        drawable.setEffect(EffectFactory.creator(EffectFactory.TOUCH_EFFECT_RIPPLE));
        drawable.setEnterDuration(2.8f);
        drawable.setShaderFactory(new TouchEffectDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                Bitmap bitmap =BitmapFactory.decodeResource(getResources(),R.mipmap.ic_shader_pic);
                return new BitmapShader(bitmap, Shader.TileMode.REPEAT,
                        Shader.TileMode.MIRROR);
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return drawable.onTouch(event);
            }
        });
    }
}
