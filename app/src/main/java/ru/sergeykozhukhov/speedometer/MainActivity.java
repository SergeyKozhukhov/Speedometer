package ru.sergeykozhukhov.speedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.SeekBar;

import ru.sergeykozhukhov.speedometer.custom_view.SpeedometerView;

public class MainActivity extends AppCompatActivity {

    private static final int COLOR_NULL = 0xFF0000FF;
    private static final int COLOR_BLUE = 0xFF0000FF;
    private static final int COLOR_DARKBLUE = 0xFF00008B;
    private static final int COLOR_WHITE = 0xFFFFFFFF;

    private SeekBar seekBar_speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SpeedometerView speedometerView = findViewById(R.id.speedometer);

        seekBar_speed = findViewById(R.id.seekBar_speed);

        int[] gradient_colors = new int[]{COLOR_BLUE, COLOR_DARKBLUE,COLOR_NULL, COLOR_WHITE, COLOR_BLUE};
        float[] gradient_points = new float[]{0.0f, 0.125f, 0.25f, 0.375f, 1.0f};


        seekBar_speed.setProgress(speedometerView.getSpeedCurrentAngle());

        /*
        * Пример настройки speedomerView
        * */

        /*speedometerView.setSpeedCurrent(30);
        speedometerView.setSpeedMax(180);
        speedometerView.setArrowColor(COLOR_BLUE);
        speedometerView.setScaleSpeedCurrent(gradient_colors, gradient_points, COLOR_WHITE);
        speedometerView.setPointCentralColor(gradient_colors, gradient_points);
        speedometerView.setSpeedDataColor(COLOR_WHITE, COLOR_WHITE);
        speedometerView.setScaleMainColor(COLOR_BLUE);*/



        seekBar_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedometerView.setSpeedCurrent(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
