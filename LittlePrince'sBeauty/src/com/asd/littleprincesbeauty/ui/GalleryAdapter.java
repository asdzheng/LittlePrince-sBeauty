/**  
 * GalleryAdapter.java
 * @version 1.0
 */
package com.asd.littleprincesbeauty.ui;

import com.asd.littleprincesbeauty.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;

public class GalleryAdapter extends BaseAdapter {

	private Context context;
	private int images[] = { R.drawable.normal_meitu, R.drawable.happy_meitu, R.drawable.angry_meitu,
										R.drawable.charming_meitu, R.drawable.kiss_meitu, R.drawable.wolf};

	public GalleryAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return images.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), images[position]);
		ZoomImageView view = new ZoomImageView(context, bmp.getWidth(), bmp.getHeight());
		view.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		view.setImageBitmap(bmp);
		return view;
	}

}
