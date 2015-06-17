package com.playdraft.hexagonimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;

public class HexagonImageView extends ImageView {
  public static final String TAG = "HexagonImageView";

  private static final PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

  private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
  private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
  private final Path hexagonPath = new Path();
  private final Rect rect = new Rect();

  private int borderSize;

  public HexagonImageView(Context context) {
    super(context);
    init();
  }

  public HexagonImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public HexagonImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    borderSize = getResources().getDimensionPixelSize(R.dimen.border_size);

    paint.setStrokeWidth(borderSize);

    borderPaint.setColor(Color.parseColor("white"));
    borderPaint.setStrokeWidth(borderSize);
    borderPaint.setDither(true);
    borderPaint.setStyle(Paint.Style.STROKE);
    borderPaint.setStrokeJoin(Paint.Join.ROUND);
    borderPaint.setStrokeCap(Paint.Cap.ROUND);
    borderPaint.setPathEffect(new CornerPathEffect(borderSize));
    borderPaint.setAntiAlias(true);
  }

  @Override protected void onSizeChanged(int width, int height, int oldw, int oldh) {
    super.onSizeChanged(width, height, oldw, oldh);

    final int centerPoint = height / 2, // center point of the hexagon
      borderOffset = borderSize / 2, // move the hexagon in by half the border size
      hexHeight = height - borderSize, // total height of hexagon
      offset = hexHeight / 4, // regular triangle
      margin = (int) (hexHeight / 2 * 0.86602540378f), // distance from center parallel distance of adjacent point
      length = hexHeight / 2; // length of one side of a hexagon

    hexagonPath.reset();
    hexagonPath.moveTo(centerPoint, borderOffset); // top
    hexagonPath.lineTo(centerPoint - margin, offset + borderOffset); // left top
    hexagonPath.lineTo(centerPoint - margin, offset + length + borderOffset); // left bottom
    hexagonPath.lineTo(centerPoint, height - borderOffset); // bottom
    hexagonPath.lineTo(centerPoint + margin, offset + length + borderOffset); // right bottom
    hexagonPath.lineTo(centerPoint + margin, offset + borderOffset); // right top
    hexagonPath.close(); //back to top

    //noinspection SuspiciousNameCombination
    rect.set(0, 0, height, height);
  }

  @Override
  protected void onDraw(@NonNull Canvas canvas) {
    Drawable drawable = getDrawable();
    if (drawable == null || getWidth() == 0 || getHeight() == 0) {
      return;
    }

    int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
    final Bitmap imageBitmap = ((BitmapDrawable) drawable).getBitmap();

    canvas.drawARGB(0, 0, 0, 0);
    paint.setXfermode(null);
    canvas.drawPath(hexagonPath, paint);

    paint.setXfermode(xfermode);
    canvas.drawBitmap(imageBitmap, rect, rect, paint);

    canvas.drawPath(hexagonPath, borderPaint);

    canvas.restoreToCount(saveCount);
  }
}
