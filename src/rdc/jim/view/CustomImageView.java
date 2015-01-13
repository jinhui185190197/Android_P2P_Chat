package rdc.jim.view;

import rdc.jim.wifip2p_coursework.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 *  * @author Administrator
 *	创建圆形ImageView
 */
public class CustomImageView extends View{
	/**
	 * TYPE_CIRCLE / TYPE_ROUND
	 */
	private int type;
	private static final int TYPE_CIRCLE = 0;
	private static final int TYPE_ROUND = 1;
	//图片资源
	private Bitmap mSrc;
	//半径
	private int mRadius;
	//控件宽度
	private int mWidth;
	//空间高度
	private int mHeight;
	

	public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		//获取控件属性
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(
				attrs, 
				R.styleable.CustomImageView, 
				defStyleAttr,
				0);
		int count = typedArray.getIndexCount();
		for(int i = 0; i<count; i++){
			int attr = typedArray.getIndex(i);
			switch (attr) {
			case R.styleable.CustomImageView_src:
				mSrc = BitmapFactory.decodeResource(getResources(), 
						typedArray.getResourceId(attr, 0));
				break;
			case R.styleable.CustomImageView_type:
				type = typedArray.getInt(attr, 0);
				break;
			case R.styleable.CustomImageView_borderRadius:
				mRadius = typedArray.getDimensionPixelSize(attr, 
						(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f,
								getResources().getDisplayMetrics()));

			default:
				break;
			}
		}
		typedArray.recycle();
	}

	public CustomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomImageView(Context context) {
		
		this(context, null);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//设置控件的大小
		//分别获取长宽大小和模式
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);

		if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
		{
			mWidth = specSize;
		} else
		{
			//计算整个控件的宽(边距+图片的宽) 
			int desireByImg = getPaddingLeft() + getPaddingRight()
					+ mSrc.getWidth();
			if (specMode == MeasureSpec.AT_MOST)// wrap_content
			{
				mWidth = Math.min(desireByImg, specSize);
			} else

				mWidth = desireByImg;
		}

		

		specMode = MeasureSpec.getMode(heightMeasureSpec);
		specSize = MeasureSpec.getSize(heightMeasureSpec);
		if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
		{
			mHeight = specSize;
		} else
		{
			int desire = getPaddingTop() + getPaddingBottom()
					+ mSrc.getHeight();

			if (specMode == MeasureSpec.AT_MOST)// wrap_content
			{
				mHeight = Math.min(desire, specSize);
			} else
				mHeight = desire;
		}

		//设置大小
		setMeasuredDimension(mWidth, mHeight);
		
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		switch (type)
		{
		// 如果是圆形
		case TYPE_CIRCLE:
			int min = Math.min(mWidth, mHeight);
			/**
			 * 长度不一致,按小的进行压缩
			 */
			mSrc = Bitmap.createScaledBitmap(mSrc, min, min, false);
			canvas.drawBitmap(createCircleImage(mSrc, min), 0, 0, null);
			break;
		case TYPE_ROUND:
			canvas.drawBitmap(createRoundConerImage(mSrc), 0, 0, null);
			break;

		}
	}
	
	private Bitmap createCircleImage(Bitmap source, int min)
	{
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
		/**
		 * 创建一个相同大小的画布
		 */
		Canvas canvas = new Canvas(target);
		/**
		 * 先绘制一个圆形
		 */
		canvas.drawCircle(min / 2, min / 2, min / 2, paint);
		/**
		 * 
		 */
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		/**
		 * 绘制图片
		 */
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}

	/**
	 * 根据原图添加圆角
	 * 
	 * @param source
	 * @return
	 */
	private Bitmap createRoundConerImage(Bitmap source)
	{
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(target);
		RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());
		canvas.drawRoundRect(rect, mRadius, mRadius, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}

}
