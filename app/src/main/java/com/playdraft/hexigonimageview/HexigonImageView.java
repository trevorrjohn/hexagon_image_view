package com.playdraft.hexigonimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

public class HexigonImageView extends ImageView {
  public static final double ANGLE = 30 * Math.PI / (double) 180; // 30 deg
  private DisplayMetrics displayMetrics;

  public HexigonImageView(Context context) {
    super(context);
    init();
  }

  public HexigonImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public HexigonImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    displayMetrics = new DisplayMetrics();
  }

  @Override
  protected void onDraw(Canvas canvas) {

    Drawable drawable = getDrawable();

    if (drawable == null) {
      return;
    }

    if (getWidth() == 0 || getHeight() == 0) {
      return;
    }

    Bitmap b = ((BitmapDrawable) drawable).getBitmap();
    Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

    int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.width);
    Bitmap roundBitmap = getRoundedCroppedBitmap(bitmap, dimensionPixelSize);
    canvas.drawBitmap(roundBitmap, 0, 0, null);

  }

  public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int width) {
    final Bitmap finalBitmap = getRecycledBitmap(bitmap, width);

    Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(), finalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(output);

    final Rect rect = new Rect(0, 0, finalBitmap.getWidth(), finalBitmap.getHeight());

    int opp = (int) (width / (double) 2 * Math.tan(ANGLE));
    int length = width - 2 * opp;

    drawImage(width, finalBitmap, canvas, rect, opp, length);
    drawBorder(width, finalBitmap, rect, opp, length, canvas);

    return output;
  }

  private static void drawBorder(int width, Bitmap finalBitmap, Rect rect, int opp, int length, Canvas border) {
    Path borderPath = getPath(width, rect, opp, length);

    Paint borderPaint = new Paint();
    borderPaint.setStrokeWidth(8);
    borderPaint.setColor(Color.parseColor("blue"));
    borderPaint.setDither(true);
    borderPaint.setStyle(Paint.Style.STROKE);
    borderPaint.setStrokeJoin(Paint.Join.ROUND);
    borderPaint.setStrokeCap(Paint.Cap.ROUND);
    borderPaint.setPathEffect(new CornerPathEffect(8));
    borderPaint.setAntiAlias(true);

    border.drawARGB(0, 0, 0, 0);
    border.drawPath(borderPath, borderPaint);
    borderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    border.drawBitmap(finalBitmap, rect, rect, borderPaint);
  }

  private static void drawImage(int width, Bitmap finalBitmap, Canvas canvas, Rect rect, int opp, int length) {
    Path path = getPath(width, rect, opp, length);

    Paint paint = new Paint();

    canvas.drawARGB(0, 0, 0, 0);
    canvas.drawPath(path, paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(finalBitmap, rect, rect, paint);
  }

  @NonNull private static Path getPath(int width, Rect rect, int opp, int length) {
    Point point1_draw = new Point(width / 2, 0);
    Point point2_draw = new Point(0, opp);
    Point point3_draw = new Point(0, opp + length);
    Point point4_draw = new Point(width / 2, width);
    Point point5_draw = new Point(width, opp + length);
    Point point6_draw = new Point(width, opp);

    Log.d("XX-VALUES", String.format("angle: %s, tan: %s,  w: %s, opp: %s, length: %s", ANGLE, Math.tan(ANGLE), width, opp, length));
    Log.d("XX-VALUES", String.format("rect %s", rect.toString()));
    Log.d("XX-POINTS", "1 - " + point1_draw.toString());
    Log.d("XX-POINTS", "2 - " + point2_draw.toString());
    Log.d("XX-POINTS", "3 - " + point3_draw.toString());
    Log.d("XX-POINTS", "4 - " + point4_draw.toString());
    Log.d("XX-POINTS", "5 - " + point5_draw.toString());
    Log.d("XX-POINTS", "6 - " + point6_draw.toString());

    Path path = new Path();
    path.moveTo(point1_draw.x, point1_draw.y);
    path.lineTo(point2_draw.x, point2_draw.y);
    path.lineTo(point3_draw.x, point3_draw.y);
    path.lineTo(point4_draw.x, point4_draw.y);
    path.lineTo(point5_draw.x, point5_draw.y);
    path.lineTo(point6_draw.x, point6_draw.y);
    path.close();
    return path;
  }

  private static Bitmap getRecycledBitmap(Bitmap bitmap, int width) {
    final Bitmap finalBitmap;
    if (bitmap.getWidth() != width || bitmap.getHeight() != width) {
      finalBitmap = Bitmap.createScaledBitmap(bitmap, width, width, false);
    } else {
      finalBitmap = bitmap;
    }
    return finalBitmap;
  }
}
