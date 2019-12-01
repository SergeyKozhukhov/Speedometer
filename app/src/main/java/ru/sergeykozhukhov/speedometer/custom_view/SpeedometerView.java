package ru.sergeykozhukhov.speedometer.custom_view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.sergeykozhukhov.speedometer.R;

/*
* Custom view спидометр
* */

public class SpeedometerView extends View {

    /*
     * Цвета
     * */
    private static final int COLOR_NULL = 0;
    private static final int COLOR_RED = 0xFFFF0000;
    private static final int COLOR_YELLOW = 0xFFFFFF00;
    private static final int COLOR_GOLD = 0xFFFFD700;
    private static final int COLOR_ORANGE = 0xFFFFA500;
    private static final int COLOR_ORANGERED = 0xFFFF4500;


    /*
     * Центр спидометра
     * */

    private static final float CENTRE_X = 365f;
    private static final float CENTRE_Y = 450f;


    /*
     * Углы скоростных шкал спидометра
     * ANGLE_START - угол начала
     * ANGLE_ARC - угол дуги
     * */

    private static final float ANGLE_START = 135f;
    private static final float ANGLE_ARC = 270f;

    /*
     * Стрелка спидометра
     * ARROW_LENGTH - длинна стрелки
     * ARROW_BOTTOM - отступ стрелки от центра спидометра
     * */
    private static final float ARROW_LENGTH = 200f;
    private static final float ARROW_BOTTOM  = 50f;


    /*
     * Ширина шкалы текущей скорости
     * */

    private static final float SCALE_SPEED_CURRENT_WIDTH = 50f;

    /*
     * Радиус фона спидометра
     * */

    private static final float RADIUS = 350f;

    /*
    * Радиус центральной точки
    * */
    
    private static final float POINT_CENTRAL_RADIUS = 25f;

    /*private static final float INDENT_SCALE_SPEED = 40.0f + SCALE_SPEED_CURRENT_WIDTH / 2;
    private static final float INDENT_SCALE_MAIN_BORDER = 20.0f;*/

    /*
    * Отсутупы
    * INDENT_SCALE_MAIN_BORDER - отступ пунктира верхней шкалы от дуги спидоиетра
    * INDENT_SCALE_SPEED - отступ шкалы скорости от дуги спидометра
    * */

    private static final float INDENT_SCALE_MAIN_BORDER = 30.0f;
    private static final float INDENT_SCALE_SPEED = 50.0f + SCALE_SPEED_CURRENT_WIDTH / 2;

    /*
    * Оси Y отображения текущей скорости
    * */
    private static final float SPEED_CURRENT_Y = CENTRE_Y + 200.0f;


    /*
    * Тени на спидометре
    * SHADOW_RADIUS - радиус
    * SHADOW_DIRECTION_X - направление по x
    * SHADOW_DIRECTION_У - направление по у
    * */
    private static final float SHADOW_RADIUS = 5.0f;
    private static final float SHADOW_DIRECTION_X = 10.0f;
    private static final float SHADOW_DIRECTION_Y = 10.0f;


    /*
    * Единица измерения скорости
    * */
    private static final String UNIT = "km/h";

    /*
    * Минимальная скорость
    * */
    private static final String SPEED_MIN = "0";

    /*
    * Настройка данных по отрисовке спидометра
    *
    * BACKGROUND - фон спидометра
    * ARROW - стрелка
    * SCALE_SPEED_CURRENT - шкала текущей скорости
    * SCALE_MAIN_ARC - дуга верхней шкалы
    * SCALE_MAIN_BORDER - разделители верхней шкалы
    * SPEED_CURRENT - текущая скорость
    * SPEED_HELP_DATA - минимальная, максимальная скорость, единицы измерения
    * POINT_CENTRAL - центр спидометра
    * AXIS_X - ось X
    * AXIS_Y - ось Y
    * AXIS_RECT - прямоугольник, внутри которого рисуется шкала текущей скорости
    * */

    private static final Paint BACKGROUND = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint ARROW = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static final Paint SCALE_SPEED_CURRENT = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static final Paint SCALE_MAIN_ARC = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint SCALE_MAIN_BORDER = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static final Paint SPEED_CURRENT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint SPEED_HELP_DATA = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static final Paint POINT_CENTRAL = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static final Paint AXIS_X = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint AXIS_Y = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint AXIS_RECT = new Paint(Paint.ANTI_ALIAS_FLAG);

    /*
     * Прямоугольник, внутри которого рисуется шкала текущей скорости
     * */
    private static final RectF RECT_SPEED_CURRENT = new RectF(
            CENTRE_X - RADIUS + INDENT_SCALE_SPEED,
            CENTRE_Y - RADIUS + INDENT_SCALE_SPEED,
            CENTRE_X + RADIUS - INDENT_SCALE_SPEED,
            CENTRE_Y + RADIUS - INDENT_SCALE_SPEED);
    /*
     * Прямоугольник, внутри которого рисуется верхняя шкала
     * */
    private static final RectF RECT_SCALE_MAIN = new RectF(
            CENTRE_X - RADIUS + INDENT_SCALE_MAIN_BORDER,
            CENTRE_Y - RADIUS + INDENT_SCALE_MAIN_BORDER,
            CENTRE_X + RADIUS - INDENT_SCALE_MAIN_BORDER,
            CENTRE_Y + RADIUS - INDENT_SCALE_MAIN_BORDER);


    /*
    * Градиент для шкалы текущей скорости и точки в центре
    * */

    private static Shader GRADIENT;

    /*
    * speed_current - текущая скорость, задаваемая, например, через seekBar, измеряется в пределах от 0 до ANGLE_ARC (270) градусов
    * speed_current_angle - преобразованная текущая скорость с интервала [0, 270] к интервалу [0, speed_max]
    * speed_max - максимальная скорость
    * */

    private int speed_current;
    private int speed_current_angle;
    private int speed_max;

    /*
    * Цвет стрелки
    * */
    @ColorInt
    private int arrow_color;


    public SpeedometerView(Context context) {
        this(context, null, 0);
        Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
    }

    public SpeedometerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();

    }

    public SpeedometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        Toast.makeText(getContext(), "3", Toast.LENGTH_SHORT).show();

    }

    public int getSpeedMax() {
        return speed_max;
    }

    public void setSpeedMax(int speed_max) {
        this.speed_max = speed_max;
    }

    /*
     * Установка цвета стрелки
     * */
    public void setArrowColor(int color)
    {
        ARROW.setColor(color);
    }

    /*
    * Установка цвета шкалы текущей скорости
    * */
    public void setScaleSpeedCurrent (int[] gradient_colors, float[] gradient_points, int shadow_color)
    {
        GRADIENT = new SweepGradient(
                CENTRE_X, CENTRE_Y,
                gradient_colors,
                gradient_points);
        SCALE_SPEED_CURRENT.setShader(GRADIENT);
        SCALE_SPEED_CURRENT.setShadowLayer(SHADOW_RADIUS, SHADOW_DIRECTION_X, SHADOW_DIRECTION_Y, shadow_color);
    }

    /*
     * Установка цвета центральной точки
     * */
    public void setPointCentralColor(int[] gradient_colors, float[] gradient_points)
    {
        GRADIENT = new SweepGradient(
                CENTRE_X, CENTRE_Y,
                gradient_colors,
                gradient_points);
        POINT_CENTRAL.setShader(GRADIENT);
    }

    /*
     * Установка цвета верхней шкалы
     * */
    public void setScaleMainColor(int color)
    {
        SCALE_MAIN_ARC.setColor(color);
        SCALE_MAIN_ARC.setShadowLayer(SHADOW_RADIUS, SHADOW_DIRECTION_X, SHADOW_DIRECTION_Y, COLOR_RED);
        SCALE_MAIN_BORDER.setColor(color);
    }

    /*
    * Установка цвета минимальной, текущей, максимальной скорости и единицы измерения
    * */
    public void setSpeedDataColor(int color, int color_shadow)
    {
        SPEED_CURRENT.setColor(color);
        SPEED_CURRENT.setShadowLayer(SHADOW_RADIUS, SHADOW_DIRECTION_X, SHADOW_DIRECTION_Y, color_shadow);
        SPEED_HELP_DATA.setColor(color);
        SPEED_HELP_DATA.setShadowLayer(SHADOW_RADIUS, SHADOW_DIRECTION_X, SHADOW_DIRECTION_Y, color_shadow);
    }

    /*
    * Установка текущей скорости
    * */
    public void setSpeedCurrent(int speedCurrent) {

        this.speed_current_angle = speedCurrent; // от 0 до 270
        this.speed_current = (int) (speed_current_angle*speed_max/ANGLE_ARC); // от 0 до speed_max
        invalidate();
    }

    public int getSpeedCurrentAngle() {
        return speed_current_angle;
    }



    /*
     * Инициализация
     * */
    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        extractAttributes(context, attrs);
        configureBackground();
        configureArrow();
        configureScaleMainArc();
        configureScaleMainBorder();
        configurePointCentral();
        configureScaleSpeedCurrent();
        configureSpeedCurrent();
        configureSpeedHelpData();
    }

    /*
     * Получение данных из xml файла
     * */
    private void extractAttributes(@NonNull Context context, @Nullable AttributeSet attrs) {

        if (attrs != null) {
            final Resources.Theme theme = context.getTheme();
            final TypedArray typedArray = theme.obtainStyledAttributes(attrs, R.styleable.SpeedometerView,0, R.style.SpeedometerViewDefault );

            int arrayResourceId_gradientColors = typedArray.getResourceId(R.styleable.SpeedometerView_gradient_colors, 0);
            int arrayResourceId_gradientPoints = typedArray.getResourceId(R.styleable.SpeedometerView_gradient_points, 0);

            int[] gradient_colors = null;
            float[] gradient_points = null;


            try {

                speed_max = typedArray.getInt(R.styleable.SpeedometerView_speed_max, 270);
                speed_current = typedArray.getInt(R.styleable.SpeedometerView_speed_current, 0);
                speed_current_angle = (int) ((float)speed_current*(ANGLE_ARC/(float)(speed_max)));
                arrow_color = typedArray.getColor(R.styleable.SpeedometerView_arrow_color, Color.RED);

                if ((arrayResourceId_gradientColors != 0) && (arrayResourceId_gradientPoints != 0)) {

                    gradient_colors = getResources().getIntArray(arrayResourceId_gradientColors);

                    int[] gradient_temp_points = getResources().getIntArray(arrayResourceId_gradientPoints);

                    if (gradient_colors.length == gradient_temp_points.length) {
                        gradient_points = new float[gradient_temp_points.length];
                        for (int i = 0; i < gradient_points.length; i++) {
                            gradient_points[i] = (float) gradient_temp_points[i] / 1000f;
                        }
                    }
                }

                GRADIENT = new SweepGradient(
                        CENTRE_X, CENTRE_Y,
                        gradient_colors,
                        gradient_points);


            } finally {
                typedArray.recycle();
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {

        /*
         * Отрисовка фона спидометра
         * */
        canvas.drawCircle(CENTRE_X, CENTRE_Y, RADIUS, BACKGROUND);

        /*
         * Отрисовка верхней шкалы скорости
         * */
        canvas.drawArc(RECT_SCALE_MAIN, ANGLE_START, ANGLE_ARC, false, SCALE_MAIN_ARC);
        canvas.drawArc(RECT_SCALE_MAIN, ANGLE_START, ANGLE_ARC, false, SCALE_MAIN_BORDER);

        /*
         * Отрисовка данных по скорости
         * */
        drawSpeedData(canvas);

        /*
         * Отрисовка шкалы текущей скорости
         * */
        canvas.drawArc(RECT_SPEED_CURRENT, ANGLE_START, speed_current_angle, false, SCALE_SPEED_CURRENT);

        /*
         * Отрисовка точки в центре
         * */
        canvas.drawCircle(CENTRE_X, CENTRE_Y, POINT_CENTRAL_RADIUS, POINT_CENTRAL);

        /*
         * Отрисовка стрелки
         * */

        canvas.rotate(speed_current_angle - 45, CENTRE_X, CENTRE_Y);
        canvas.drawLine(CENTRE_X - ARROW_BOTTOM, CENTRE_Y, CENTRE_X - ARROW_LENGTH, CENTRE_Y, ARROW);
        canvas.restore();

        /*
         * Отрисовка системы координат
         * */

        //canvas.drawLine(CENTRE_X-ARROW_LENGTH, CENTRE_Y, CENTRE_X+ARROW_LENGTH, CENTRE_Y, AXIS_X);
        //canvas.drawLine(CENTRE_X, CENTRE_Y-ARROW_LENGTH, CENTRE_X, CENTRE_Y+ARROW_LENGTH, AXIS_Y);
        //canvas.drawRect(RECT_SPEED_CURRENT, AXIS_RECT);*//*
    }


    /*
     * Настройка фона спидометра (круга)
     * */
    private void configureBackground() {
        BACKGROUND.setColor(Color.BLACK);
        BACKGROUND.setStyle(Paint.Style.FILL);
    }

    /*
     * Настройка стрелки
     * */
    private void configureArrow() {
        ARROW.setColor(arrow_color);
        ARROW.setStyle(Paint.Style.FILL_AND_STROKE);
        ARROW.setStrokeWidth(3);
    }

    /*
     * Настройка шкалы текущей скорости
     * */
    private void configureScaleSpeedCurrent() {

        SCALE_SPEED_CURRENT.setShader(GRADIENT);
        SCALE_SPEED_CURRENT.setStyle(Paint.Style.STROKE);
        SCALE_SPEED_CURRENT.setStrokeWidth(SCALE_SPEED_CURRENT_WIDTH);
        SCALE_SPEED_CURRENT.setPathEffect(new DashPathEffect(new float[]{5, 20}, 0));
        SCALE_SPEED_CURRENT.setShadowLayer(SHADOW_RADIUS, SHADOW_DIRECTION_X, SHADOW_DIRECTION_Y, COLOR_GOLD);
    }

    /*
     * Настройка точки в центре
     * */
    private void configurePointCentral() {

        POINT_CENTRAL.setShader(GRADIENT);
        POINT_CENTRAL.setShadowLayer(SHADOW_RADIUS, SHADOW_DIRECTION_X, SHADOW_DIRECTION_Y, COLOR_RED);
    }

    /*
     * Настройка дуги верхней шкалы
     * */
    private void configureScaleMainArc() {
        SCALE_MAIN_ARC.setColor(COLOR_RED);
        SCALE_MAIN_ARC.setStyle(Paint.Style.STROKE);
        SCALE_MAIN_ARC.setStrokeWidth(3);
        SCALE_MAIN_ARC.setShadowLayer(SHADOW_RADIUS, SHADOW_DIRECTION_X, SHADOW_DIRECTION_Y, COLOR_RED);
    }

    /*
     * Настройка разделителей верхней шкалы
     * */
    private void configureScaleMainBorder() {

        int border_number = 8;
        float radius = (RECT_SCALE_MAIN.right - RECT_SPEED_CURRENT.left)/2f;
        float border_width = 10;
        float space = (float) (((Math.PI * radius)*(ANGLE_ARC/180.f) - (float)border_number*border_width)/(float)(border_number-1));

        SCALE_MAIN_BORDER.setColor(COLOR_RED);
        SCALE_MAIN_BORDER.setStyle(Paint.Style.STROKE);
        SCALE_MAIN_BORDER.setStrokeWidth(30);
        SCALE_MAIN_BORDER.setPathEffect(new DashPathEffect(new float[]{border_width, space}, 0));
        SCALE_MAIN_BORDER.setShadowLayer(SHADOW_RADIUS, SHADOW_DIRECTION_X, SHADOW_DIRECTION_Y, COLOR_RED);
    }

    /*
     * Настройка текущей скорости
     * */
    private void configureSpeedCurrent() {
        SPEED_CURRENT.setAntiAlias(true);
        SPEED_CURRENT.setColor(Color.YELLOW);
        SPEED_CURRENT.setTextSize(100.0f);
        SPEED_CURRENT.setStrokeWidth(2.0f);
        SPEED_CURRENT.setStyle(Paint.Style.STROKE);
        SPEED_CURRENT.setShadowLayer(SHADOW_RADIUS, SHADOW_DIRECTION_X, SHADOW_DIRECTION_Y, COLOR_GOLD);
    }

    /*
    * Настройка минимальной, максимальной скорости и единицы измерения
    * */
    private void configureSpeedHelpData() {

        SPEED_HELP_DATA.setAntiAlias(true);
        SPEED_HELP_DATA.setColor(Color.YELLOW);
        SPEED_HELP_DATA.setTextSize(50.0f);
        SPEED_HELP_DATA.setStrokeWidth(2.0f);
        SPEED_HELP_DATA.setStyle(Paint.Style.STROKE);
        SPEED_HELP_DATA.setShadowLayer(SHADOW_RADIUS, SHADOW_DIRECTION_X, SHADOW_DIRECTION_Y, COLOR_GOLD);
    }

    /*
    * Отрисовка данных по скорости
    * */
    private void drawSpeedData(Canvas canvas) {

        /*
         * Прямоугольник, описывающий данные скорости
         * */
        Rect rect_size_text = new Rect();

        /*
        * Отступа от оси центра минимальной и максимальной скорости
        * */
        float indent = 150.0f;

        /*
        * Отрисовка текущей скорости
        * */
        String speed_current_text = String.valueOf(speed_current);
        SPEED_CURRENT.getTextBounds(speed_current_text, 0, speed_current_text.length(), rect_size_text);
        canvas.drawText(speed_current_text, CENTRE_X - rect_size_text.width()/2f, SPEED_CURRENT_Y, SPEED_CURRENT);

        // МОЖНО ВЫНЕСТИ

        /*
        * Отрисовка единицы измерения скорости
        * */
        SPEED_HELP_DATA.getTextBounds(UNIT, 0, UNIT.length(), rect_size_text);
        canvas.drawText(UNIT, CENTRE_X - rect_size_text.width()/2, CENTRE_Y + RADIUS -30f, SPEED_HELP_DATA);


        /*
        * Отрисовка минимальной скорости
        * */
        SPEED_HELP_DATA.getTextBounds(SPEED_MIN, 0, SPEED_MIN.length(), rect_size_text);
        canvas.drawText(SPEED_MIN, CENTRE_X - indent - rect_size_text.width()/2, SPEED_CURRENT_Y + 60, SPEED_HELP_DATA);

        /*
        * Отрисовка максимальной скорости
        * */
        String speed_max_text = String.valueOf(speed_max);
        SPEED_HELP_DATA.getTextBounds(speed_max_text, 0, speed_max_text.length(), rect_size_text);
        canvas.drawText(speed_max_text, CENTRE_X + indent - rect_size_text.width()/2, SPEED_CURRENT_Y + 60, SPEED_HELP_DATA);
    }

    /*
     * Настройка системы координат
     * */
    private void configureSystemOfAxes() {

        AXIS_X.setColor(Color.GREEN);
        AXIS_X.setStyle(Paint.Style.FILL_AND_STROKE);
        AXIS_X.setStrokeWidth(3);

        AXIS_Y.setColor(Color.GREEN);
        AXIS_Y.setStyle(Paint.Style.FILL_AND_STROKE);
        AXIS_Y.setStrokeWidth(3);

        AXIS_RECT.setColor(Color.GREEN);
        AXIS_RECT.setStyle(Paint.Style.STROKE);
        AXIS_RECT.setStrokeWidth(3);
    }


}
