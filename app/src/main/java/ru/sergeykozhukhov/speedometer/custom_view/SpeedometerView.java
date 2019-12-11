package ru.sergeykozhukhov.speedometer.custom_view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
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
    private float centre_x = 350f;
    private float centre_y = 350f;


    /*
     * Углы скоростных шкал спидометра
     * ANGLE_START - угол начала
     * ANGLE_ARC - угол дуги
     * */
    private static final float ANGLE_START = 135f;
    private static final float ANGLE_ARC = 270f;

    /*
    * point_central_radius - радиус центральной точки
    * from_central_point_to_arrow_bottom - расстояние от верхней стороны центральной точки до нижней части стрелки
    * arrow_length - длинна стрелки
    * from_arrow_top_to_scale_speed - расстояние от верхней части стрелки до нижней части шкалы текущей скорости
    * scale_speed_current_width - ширина шкалы текущей скорости
    * from_scale_speed_current_to_scale_main - расстояние от верхней части шкалы текущей скорости до нижней части верхней шкалы
    * scale_main_border_width - ширина верхней шкалы
    * from_scale_main_border_to_radius - расстояние от верхней части "верхней шкалы" до дуги спидометра
    * radius - радиус спидометра
    * */
    private float point_central_radius = 25.0f;
    private float from_central_point_to_arrow_bottom = 25.0f;
    private float arrow_length = 150.0f;
    private float from_arrow_top_to_scale_speed = 50.0f;
    private float scale_speed_current_width = 50.0f;
    private float from_scale_speed_current_to_scale_main = 5.0f;
    private float scale_main_border_width = 30.0f;
    private float from_scale_main_border_to_radius = 15.0f;
    private float radius;

    /*
    * arrow_bottom - расстояние от центра спидометра до нижней части стрелки
    * arrow_top - расстояние от центра спидометра до верхней части стрелки
    * scale_speed_current - расстояние от центра спидометра до центра шкалы текущей скорости
    * scale_main - расстояние от центра спидометра до центра верхней шкалы
    * */
    private float arrow_bottom;
    private float arrow_top;
    private float scale_speed_current;
    private float scale_main;

    /*
    * from_centre_to_speed_data_current_y - расстояние от центра спидометра до места отображения текущей скорости (по оси y)
    * from_speed_data_current_to_min_max_y - расстояние от места отображения текущей скорости до места отображения минимального и максимального значения скорости (по оси y)
    * from_help_data_min_max_to_unit - расстояние места отображения минимального и максимального значения скорости до места отображения единицы измерения скорости ((по оси y)
    * speed_help_data_min_max_centre_x - расстояние от центра спидометра до центра отображения минимального и максимального значения скорости (по оси x)
    * */
    private float from_centre_to_speed_data_current_y = 200.0f;
    private float from_speed_data_current_to_min_max_y = 60.0f;
    private float from_help_data_min_max_to_unit = 60.0f;
    private float speed_help_data_min_max_centre_x = 150.0f;

    /*
     * speed_data_current_y - расстояние от 0 до до места отображения текущей скорости (по оси y)
     * speed_help_data_min_max_y - расстояние от 0 до до места отображения минимального и максимального значения скорости (по оси y)
     * speed_help_data_unit - расстояние от 0 до до места отображения единицы измерения скорости (по оси y)
     * */
    private float speed_data_current_y;
    private float speed_help_data_min_max_y;
    private float speed_help_data_unit;

    /*
    * speed_data_current_text_size - размер отображения значения текущей скорости
    * speed_help_data_text_size - размер отображения значения min, max скорости и единицы измерения скорости
    * */
    private float speed_data_current_text_size = 100.0f;
    private float speed_help_data_text_size = 50.0f;
    
    /*
    * Тени на спидометре
    * shadow_radius - радиус
    * shadow_direction_x - направление по x
    * shadow_direction_у - направление по у
    * */
    private float shadow_radius = 5.0f;
    private float shadow_direction_x = 10.0f;
    private float shadow_direction_y = 10.0f;


    /*
     * rect_speed_current - прямоугольник, внутри которого рисуется шкала текущей скорости
     * rect_scale_main - прямоугольник, внутри которого рисуется верхняя шкала
     * rect_size_text -  прямоугольник, описывающий данные скорости (текстовое представление)
     * */
    private RectF rect_speed_current;
    private RectF rect_scale_main;
    private Rect rect_size_text;

    /*
    * figure_width - ширина контура ряда фигур
    * text_width - ширина контура текстового отображения данных скорости
    * border_width - ширина разделителя верхней шкалы
    * scale_speed_current_gap - расстояние до нового разделителя на шкале текущей скорости
    * scale_speed_current_border_width - ширина разделителя на шкале текущей скорости
    * */
    private float figure_width = 3.0f;
    private float text_width = 2.0f;
    private float border_width = 10.0f;
    private float scale_speed_current_gap = 20.0f;
    private float scale_speed_current_border_width = 5.0f;

    /*
     * speed_current - текущая скорость, задаваемая, например, через seekBar, измеряется в пределах от 0 до ANGLE_ARC (270) градусов
     * speed_current_angle - преобразованная текущая скорость с интервала [0, 270] к интервалу [0, speed_max]
     * speed_max - максимальная скорость
     * */
    private int speed_current;
    private int speed_current_angle;
    private int speed_max;


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

    private final Paint BACKGROUND = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ARROW = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint SCALE_SPEED_CURRENT = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint SCALE_MAIN_ARC = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint SCALE_MAIN_BORDER = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint SPEED_CURRENT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint SPEED_HELP_DATA = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint POINT_CENTRAL = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint AXIS_X = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint AXIS_Y = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint AXIS_RECT = new Paint(Paint.ANTI_ALIAS_FLAG);

    /*
    * Градиент для шкалы текущей скорости и точки в центре
    * */
    private Shader gradient;

    /*
    * Цвет стрелки
    * */
    @ColorInt
    private int arrow_color;

    /*
    * scale_speed_current_shadow_color - цвет тени шкалы текушей скорости
    * scale_main_shadow_color - цвет тени верхней шкалы
    * speed_data_shadow_color - цвет тени текстового представления данных скорости
    * */
    private int scale_speed_current_shadow_color = COLOR_RED;
    private int scale_main_shadow_color = COLOR_RED;
    private int speed_data_shadow_color = COLOR_GOLD;

    /*
     * UNIT - единица измерения скорости
     * SPEED_MIN - минимальная скорость
     * */
    private static final String UNIT = "km/h";
    private static final String SPEED_MIN = "0";

    private static final String TAG = "SpeedometerView";


    public SpeedometerView(Context context) {
        this(context, null, 0);
    }

    public SpeedometerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public SpeedometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        extractAttributes(context, attrs);
        initValues();
        initPaints();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float desiredWidth = Math.max(2*centre_x, getSuggestedMinimumWidth()) + getPaddingLeft() + getPaddingRight();
        float desiredHeight = Math.max(2*centre_y, getSuggestedMinimumHeight()) + getPaddingTop() + getPaddingBottom();
        int desiredSize = (int) (Math.max(desiredHeight, desiredWidth));
        final int resolvedWidth = resolveSize(desiredSize, widthMeasureSpec);
        final int resolvedHeight = resolveSize(desiredSize, heightMeasureSpec);
        setMeasuredDimension(resolvedWidth, resolvedHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        float centre_x_old = centre_x;
        float centre_y_old = centre_y;

        float new_size_speedometer = (float)Math.min(w-getPaddingLeft()-getPaddingRight(), h-getPaddingTop()-getPaddingBottom());

        float proportion = new_size_speedometer/(2.0f*radius);

        changeSizeSpeedometer(proportion);
        setNewCentreSpeedometer(w, h);
        initValues();
        changeCentreGradient(centre_x_old, centre_y_old, centre_x, centre_y);
        setNewParamsPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        /*
         * Отрисовка фона спидометра
         * */
        canvas.drawCircle(centre_x, centre_y, radius, BACKGROUND);

        /*
         * Отрисовка верхней шкалы скорости
         * */
        canvas.drawArc(rect_scale_main, ANGLE_START, ANGLE_ARC, false, SCALE_MAIN_ARC);
        canvas.drawArc(rect_scale_main, ANGLE_START, ANGLE_ARC, false, SCALE_MAIN_BORDER);

        /*
         * Отрисовка данных по скорости
         * */
        drawSpeedData(canvas);

        /*
         * Отрисовка шкалы текущей скорости
         * */
        canvas.drawArc(rect_speed_current, ANGLE_START, speed_current_angle, false, SCALE_SPEED_CURRENT);

        /*
         * Отрисовка точки в центре
         * */
        canvas.drawCircle(centre_x, centre_y, point_central_radius, POINT_CENTRAL);

        /*
         * Отрисовка стрелки
         * */

        canvas.rotate(speed_current_angle - 45, centre_x, centre_y);
        canvas.drawLine(centre_x - arrow_bottom, centre_y, centre_x - arrow_top, centre_y, ARROW);
        canvas.restore();

        canvas.drawCircle(centre_x, centre_y, 10, POINT_CENTRAL);

        /*
         * Отрисовка системы координат
         * */

        //canvas.drawLine(centre_x-ARROW_LENGTH, centre_y, centre_x+ARROW_LENGTH, centre_y, AXIS_X);
        //canvas.drawLine(centre_x, centre_y-ARROW_LENGTH, centre_x, centre_y+ARROW_LENGTH, AXIS_Y);
        //canvas.drawRect(rect_speed_current, AXIS_RECT);*//*
        //canvas.drawRect(0, 0, getRight(), getBottom(), AXIS_RECT);
        //canvas.drawRect(getPaddingLeft(), getPaddingTop(), getRight()-getPaddingRight(), getBottom()-getPaddingBottom(), AXIS_RECT);
    }


    /*
     * [PUBLIC FUNCTIONS]
     * */

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
    public void setScaleSpeedCurrent (int[] gradient_colors, float[] gradient_points, int shadow_color){
        gradient = new SweepGradient(
                centre_x, centre_y,
                gradient_colors,
                gradient_points);
        SCALE_SPEED_CURRENT.setShader(gradient);
        SCALE_SPEED_CURRENT.setShadowLayer(shadow_radius, shadow_direction_x, shadow_direction_y, shadow_color);
    }

    /*
     * Установка цвета центральной точки
     * */
    public void setPointCentralColor(int[] gradient_colors, float[] gradient_points){
        gradient = new SweepGradient(
                centre_x, centre_y,
                gradient_colors,
                gradient_points);
        POINT_CENTRAL.setShader(gradient);
    }

    /*
     * Установка цвета верхней шкалы
     * */
    public void setScaleMainColor(int color){
        SCALE_MAIN_ARC.setColor(color);
        SCALE_MAIN_ARC.setShadowLayer(shadow_radius, shadow_direction_x, shadow_direction_y, COLOR_RED);
        SCALE_MAIN_BORDER.setColor(color);
        scale_main_shadow_color = color;
    }

    /*
    * Установка цвета минимальной, текущей, максимальной скорости и единицы измерения
    * */
    public void setSpeedDataColor(int color, int color_shadow){
        SPEED_CURRENT.setColor(color);
        SPEED_CURRENT.setShadowLayer(shadow_radius, shadow_direction_x, shadow_direction_y, color_shadow);
        SPEED_HELP_DATA.setColor(color);
        SPEED_HELP_DATA.setShadowLayer(shadow_radius, shadow_direction_x, shadow_direction_y, color_shadow);
        speed_data_shadow_color = color_shadow;
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
    * [PRIVATE FUNCTIONS]
    * */

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

                gradient = new SweepGradient(
                        centre_x, centre_y,
                        gradient_colors,
                        gradient_points);


            } finally {
                typedArray.recycle();
            }
        }
    }

    /*
    * Инициализация переменных
    * */
    private void initValues()
    {
        radius = point_central_radius +
                from_central_point_to_arrow_bottom +
                arrow_length +
                from_arrow_top_to_scale_speed +
                scale_speed_current_width +
                from_scale_speed_current_to_scale_main +
                scale_main_border_width +
                from_scale_main_border_to_radius;

        arrow_bottom = point_central_radius + from_central_point_to_arrow_bottom;
        arrow_top = point_central_radius + from_central_point_to_arrow_bottom + arrow_length;

        scale_speed_current = arrow_top + from_arrow_top_to_scale_speed + scale_speed_current_width/2.0f;
        scale_main = scale_speed_current + scale_speed_current_width/2.0f + from_scale_speed_current_to_scale_main + scale_main_border_width/2.0f;

        speed_data_current_y = centre_y + from_centre_to_speed_data_current_y;
        speed_help_data_min_max_y = speed_data_current_y + from_speed_data_current_to_min_max_y;
        speed_help_data_unit = speed_help_data_min_max_y + from_help_data_min_max_to_unit;

        rect_speed_current = new RectF(
                centre_x - scale_speed_current,
                centre_y - scale_speed_current,
                centre_x + scale_speed_current,
                centre_y + scale_speed_current);

        rect_scale_main = new RectF(
                centre_x - scale_main,
                centre_y - scale_main,
                centre_x + scale_main,
                centre_y + scale_main);
        rect_size_text = new Rect();
    }

    /*
    * Изменение размера спидометра
    * value - во сколько раз увеличить
    * */
    private void changeSizeSpeedometer(float value)
    {
        centre_x *= value;
        centre_y *= value;

        point_central_radius *= value;
        from_central_point_to_arrow_bottom *= value;
        arrow_length *= value;
        from_arrow_top_to_scale_speed *= value;
        scale_speed_current_width *= value;
        from_scale_speed_current_to_scale_main *= value;
        scale_main_border_width *= value;
        from_scale_main_border_to_radius *= value;

        from_centre_to_speed_data_current_y*=value;
        from_speed_data_current_to_min_max_y *= value;
        from_help_data_min_max_to_unit *= value;

        speed_help_data_min_max_centre_x *= value;

        speed_data_current_text_size *= value;
        speed_help_data_text_size *= value;

        shadow_radius *= value;
        shadow_direction_x *= value;
        shadow_direction_y *= value;

        figure_width *= value;
        text_width *= value;
        border_width *= value;
        scale_speed_current_gap *= value;
        scale_speed_current_border_width *= value;
    }

    /*
     * Установка новой позиции центра спидометра с учетом отступов
     * */
    private void setNewCentreSpeedometer(float w, float h)
    {
        float avgX = (w - getPaddingLeft() - getPaddingRight())/2.0f;
        float avgY = (h - getPaddingTop() - getPaddingBottom())/2.0f;

        centre_x = getPaddingLeft() + avgX;
        centre_y = getPaddingTop() + avgY;
    }

    /*
     * Изменение центра градиента
     * */
    private void changeCentreGradient(float x_old, float y_old, float x_new, float y_new)
    {
        // если функция вызывается дважды, градиент преобразуется дважды, центр смещен, для разового вызова все работае нормально
        // можно, например, сохранять цвета градиента и заново его инициализировать с этими параметрами и центром
        Matrix matrix = new Matrix();

        float modPathX = Math.abs(x_new - x_old);
        float modPathY = Math.abs(y_new - y_old);

        if (x_old <= x_new && y_old <= y_new)
            matrix.setTranslate(modPathX, modPathY);
        else if (x_old <= x_new && y_old >= y_new)
            matrix.setTranslate(modPathX, -modPathY);
        else if (x_old >= x_new && y_old <= y_new)
            matrix.setTranslate(-modPathX, modPathY);
        else if (x_old >= x_new && y_old >= y_new)
            matrix.setTranslate(-modPathX, -modPathY);
        else
            return;
        gradient.setLocalMatrix(matrix);
    }

    /*
     * Инициализация начальных данных отображения спидометра
     * */
    private void initPaints() {
        configureBackground();
        configureArrow();
        configureScaleMainArc();
        configureScaleMainBorder();
        configurePointCentral();
        configureScaleSpeedCurrent();
        configureSpeedCurrent();
        configureSpeedHelpData();
        configureSystemOfAxes();
    }

    /*
    * Обновление параметров отображения в соответствии с новыми размерами
    * */
    private void setNewParamsPaint(){

        ARROW.setStrokeWidth(figure_width);

        SCALE_SPEED_CURRENT.setStrokeWidth(scale_speed_current_width);
        SCALE_SPEED_CURRENT.setPathEffect(new DashPathEffect(new float[]{scale_speed_current_border_width, scale_speed_current_gap}, 0));
        SCALE_SPEED_CURRENT.setShadowLayer(shadow_radius, shadow_direction_x, shadow_direction_y, scale_speed_current_shadow_color);

        SCALE_MAIN_ARC.setStrokeWidth(figure_width);
        SCALE_MAIN_ARC.setShadowLayer(shadow_radius, shadow_direction_x, shadow_direction_y, scale_main_shadow_color);

        int border_number = 8;
        float radius = (rect_scale_main.right - rect_speed_current.left) / 2f;
        float space = (float) (((Math.PI * radius) * (ANGLE_ARC / 180.f) - (float) border_number * border_width) / (float) (border_number - 1));

        SCALE_MAIN_BORDER.setStrokeWidth(scale_main_border_width);
        SCALE_MAIN_BORDER.setPathEffect(new DashPathEffect(new float[]{border_width, space}, 0));
        SCALE_MAIN_BORDER.setShadowLayer(shadow_radius, shadow_direction_x, shadow_direction_y, scale_main_shadow_color);

        SPEED_CURRENT.setTextSize(speed_data_current_text_size);
        SPEED_CURRENT.setStrokeWidth(text_width);
        SPEED_CURRENT.setShadowLayer(shadow_radius, shadow_direction_x, shadow_direction_y, speed_data_shadow_color);

        SPEED_HELP_DATA.setTextSize(speed_help_data_text_size);
        SPEED_HELP_DATA.setStrokeWidth(text_width);
        SPEED_HELP_DATA.setShadowLayer(shadow_radius, shadow_direction_x, shadow_direction_y, speed_data_shadow_color);

        SCALE_SPEED_CURRENT.setShader(gradient);
        POINT_CENTRAL.setShader(gradient);
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
        ARROW.setStrokeWidth(figure_width);
    }

    /*
     * Настройка шкалы текущей скорости
     * */
    private void configureScaleSpeedCurrent() {

        SCALE_SPEED_CURRENT.setShader(gradient);
        SCALE_SPEED_CURRENT.setStyle(Paint.Style.STROKE);
        SCALE_SPEED_CURRENT.setStrokeWidth(scale_speed_current_width);
        SCALE_SPEED_CURRENT.setPathEffect(new DashPathEffect(new float[]{scale_speed_current_border_width, scale_speed_current_gap}, 0));
        SCALE_SPEED_CURRENT.setShadowLayer(shadow_radius, shadow_direction_x, shadow_direction_y, scale_speed_current_shadow_color);
    }

    /*
     * Настройка точки в центре
     * */
    private void configurePointCentral() {
        POINT_CENTRAL.setShader(gradient);
    }

    /*
     * Настройка дуги верхней шкалы
     * */
    private void configureScaleMainArc() {

        SCALE_MAIN_ARC.setColor(COLOR_RED);
        SCALE_MAIN_ARC.setStyle(Paint.Style.STROKE);
        SCALE_MAIN_ARC.setStrokeWidth(figure_width);
        SCALE_MAIN_ARC.setShadowLayer(shadow_radius, shadow_direction_x, shadow_direction_y, scale_main_shadow_color);
    }

    /*
     * Настройка разделителей верхней шкалы
     * */
    private void configureScaleMainBorder() {

        int border_number = 8;
        float radius = (rect_scale_main.right - rect_speed_current.left)/2f;
        float space = (float) (((Math.PI * radius)*(ANGLE_ARC/180.f) - (float)border_number*border_width)/(float)(border_number-1));

        SCALE_MAIN_BORDER.setColor(COLOR_RED);
        SCALE_MAIN_BORDER.setStyle(Paint.Style.STROKE);
        SCALE_MAIN_BORDER.setStrokeWidth(scale_main_border_width);
        SCALE_MAIN_BORDER.setPathEffect(new DashPathEffect(new float[]{border_width, space}, 0));
        SCALE_MAIN_BORDER.setShadowLayer(shadow_radius, shadow_direction_x, shadow_direction_y, scale_main_shadow_color);
    }

    /*
     * Настройка текущей скорости
     * */
    private void configureSpeedCurrent() {

        SPEED_CURRENT.setColor(Color.YELLOW);
        SPEED_CURRENT.setTextSize(speed_data_current_text_size);
        SPEED_CURRENT.setStrokeWidth(text_width);
        SPEED_CURRENT.setStyle(Paint.Style.STROKE);
        SPEED_CURRENT.setShadowLayer(shadow_radius, shadow_direction_x, shadow_direction_y, speed_data_shadow_color);
    }

    /*
    * Настройка минимальной, максимальной скорости и единицы измерения
    * */
    private void configureSpeedHelpData() {

        SPEED_HELP_DATA.setColor(Color.YELLOW);
        SPEED_HELP_DATA.setTextSize(speed_help_data_text_size);
        SPEED_HELP_DATA.setStrokeWidth(text_width);
        SPEED_HELP_DATA.setStyle(Paint.Style.STROKE);
        SPEED_HELP_DATA.setShadowLayer(shadow_radius, shadow_direction_x, shadow_direction_y, speed_data_shadow_color);
    }

    /*
    * Отрисовка данных по скорости
    * */
    private void drawSpeedData(Canvas canvas) {

        /*
        * Отрисовка текущей скорости
        * */
        String speed_current_text = String.valueOf(speed_current);
        SPEED_CURRENT.getTextBounds(speed_current_text, 0, speed_current_text.length(), rect_size_text);
        canvas.drawText(speed_current_text, centre_x - rect_size_text.width()/2.0f, speed_data_current_y, SPEED_CURRENT);

        // МОЖНО ВЫНЕСТИ

        /*
        * Отрисовка единицы измерения скорости
        * */
        SPEED_HELP_DATA.getTextBounds(UNIT, 0, UNIT.length(), rect_size_text);
        canvas.drawText(UNIT, centre_x - rect_size_text.width()/2, speed_help_data_unit, SPEED_HELP_DATA);


        /*
        * Отрисовка минимальной скорости
        * */
        SPEED_HELP_DATA.getTextBounds(SPEED_MIN, 0, SPEED_MIN.length(), rect_size_text);
        canvas.drawText(SPEED_MIN, centre_x - speed_help_data_min_max_centre_x - rect_size_text.width()/2, speed_help_data_min_max_y, SPEED_HELP_DATA);

        /*
        * Отрисовка максимальной скорости
        * */
        String speed_max_text = String.valueOf(speed_max);
        SPEED_HELP_DATA.getTextBounds(speed_max_text, 0, speed_max_text.length(), rect_size_text);
        canvas.drawText(speed_max_text, centre_x + speed_help_data_min_max_centre_x - rect_size_text.width()/2, speed_help_data_min_max_y, SPEED_HELP_DATA);
    }

    /*
     * Настройка системы координат
     * */
    private void configureSystemOfAxes() {

        AXIS_X.setColor(Color.GREEN);
        AXIS_X.setStyle(Paint.Style.FILL_AND_STROKE);
        AXIS_X.setStrokeWidth(figure_width);

        AXIS_Y.setColor(Color.GREEN);
        AXIS_Y.setStyle(Paint.Style.FILL_AND_STROKE);
        AXIS_Y.setStrokeWidth(figure_width);

        AXIS_RECT.setColor(Color.GREEN);
        AXIS_RECT.setStyle(Paint.Style.STROKE);
        AXIS_RECT.setStrokeWidth(figure_width);
    }

}
