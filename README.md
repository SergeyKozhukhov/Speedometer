# Speedometer

[Speedometer]

Custom View. Пользовательский компонент, имитирующий деятельность спидометра.

[ФУНКЦИИ]

Возможности:

1. Отображение скорости на спидометре;
2. настройка цветовых и численных значений спидометра;
3. возможность настройки спидометра через атрибуты из xml файла;
4. наличие цветовой схемы и значений по умолчанию.

[ТЕХНИЧЕСКАЯ ЧАСТЬ]

Использование:

- наследование от View;
- рисование с применением Canvas;
- declare-styleable.

Открытые методы:

1. public void setSpeedMax(int speed_max) - установка максимальной скорости;
2. public int getSpeedMax() - получение максимальной скорости;
3. public void setSpeedCurrent(int speedCurrent) - установка текущей скорости;
4. public int getSpeedCurrentAngle() - получение текущей скорости;
5. public void setArrowColor(int color) - установка цвета стрелки;
6. public void setScaleSpeedCurrent (int[] gradient_colors, float[] gradient_points, int shadow_color) - установка градиента пунктирной шкалы, отображающей значение текущей скорости. Принимает список цветов для градиента, их положение на окружности [0; 1000];
7. public void setPointCentralColor(int[] gradient_colors, float[] gradient_points) - установка градиента точки в центре. Принимает список цветов для градиента, их положение на окружности [0; 1000];
8. public void setScaleMainColor(int color) - установка цвета для шкалы вдоль окружности;
9. public void setSpeedDataColor(int color, int color_shadow) - установка цвета отображения текстовой информации о скорости.

Атрибуты спидометра:

- "speed_current" - текущая скорость (format="integer");
- "speed_max" - максимальная скорость (format="integer");
- "arrow_color"  - цвет стрелки (format="color");
- "gradient_colors" - ссылка на массив цветов градиента (format="reference");
- "gradient_points" - ссылка на массив точек цветов градиента (format="reference").

[ПРИМЕР РАБОТЫ ПРОГРАММЫ]

1. Стандартная схема спидометра, установленная по умолчанию.

![Image alt](/scr/01_01.jpg)

2. Возможный вариант настройки спидометра.

![Image alt](/scr/01_02.jpg)
