package com.codepath.android.lollipopexercise.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.android.lollipopexercise.R;
import com.codepath.android.lollipopexercise.activities.DetailsActivity;
import com.codepath.android.lollipopexercise.models.Contact;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class CardAdapter extends CursorRecyclerViewAdapter<CardAdapter.VH> {

    public CardAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(CardAdapter.VH viewHolder, Cursor cursor) {
        final String id = cursor.getString(0);
        final String name = cursor.getString(1);
        final String thumbnailUri = cursor.getString(2);
        final Contact contact = new Contact(id, thumbnailUri, name);
        populate(getContext(), viewHolder, contact);
    }

    @Override
    public CardAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_grid, parent, false);
        return new VH(itemView, getContext());
    }

    public static void populate(final Context context, final VH viewHolder, final Contact contact) {
        final Contact currentContact = (Contact)viewHolder.rootView.getTag();
        if (currentContact != null && currentContact.id.equals(contact.id)) return;
        viewHolder.rootView.setTag(contact);
        viewHolder.tvName.setText(contact.name);
        viewHolder.ivProfile.setImageBitmap(null);
        viewHolder.vPalette.setBackgroundColor(contact.getColor());
        Picasso.with(context).load(Uri.parse(contact.thumbnailUri)).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (viewHolder.rootView.getTag() == contact) {
                    viewHolder.ivProfile.setImageBitmap(bitmap);
                    if (contact.getColor() == Color.GRAY) {
                        if (viewHolder.vPalette != null) {
                            Palette.generateAsync(bitmap, 32, new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    if (viewHolder.rootView.getTag() == contact) {
                                        int color = palette.getVibrantColor(Color.GRAY);
                                        viewHolder.vPalette.setBackgroundColor(color);
                                        contact.setColor(color);
                                    }
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e("CardAdapter", "Failed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    public static class VH extends RecyclerView.ViewHolder {
        final View rootView;
        final ImageView ivProfile;
        final TextView tvName;
        final View vPalette;
        public VH(View itemView, final Context context) {
            super(itemView);
            ivProfile = (ImageView)itemView.findViewById(R.id.ivProfile);
            tvName = (TextView)itemView.findViewById(R.id.tvName);
            vPalette = itemView.findViewById(R.id.vPalette);
            rootView = itemView;
            if (context instanceof DetailsActivity) return;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Contact contact = (Contact)v.getTag();
                    if (contact != null) {
                        final Intent intent = new Intent(context, DetailsActivity.class);
                        intent.putExtra(DetailsActivity.EXTRA_CONTACT, contact);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
