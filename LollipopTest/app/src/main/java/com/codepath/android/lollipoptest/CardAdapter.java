package com.codepath.android.lollipoptest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class CardAdapter extends CursorRecyclerViewAdapter<CardAdapter.VH> {
    public CardAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(VH viewHolder, Cursor cursor) {
        final String id = cursor.getString(0);
        final String name = cursor.getString(1);
        final String thumbnailUri = cursor.getString(2);
        final Contact contact = new Contact(id, thumbnailUri, name);
        populate(getContext(), viewHolder, contact);
    }

    public static void populate(final Context context, final VH viewHolder, final Contact contact) {
        viewHolder.rootView.setTag(contact);
        viewHolder.tvName.setText(contact.name);
        viewHolder.ivProfile.setImageBitmap(null);
        if (viewHolder.vPalette != null) {
            viewHolder.vPalette.setBackgroundColor(Color.GRAY);
        }
        Picasso.with(context).load(Uri.parse(contact.thumbnailUri)).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (viewHolder.rootView.getTag() == contact) {
                    viewHolder.ivProfile.setImageBitmap(bitmap);
                    if (viewHolder.vPalette != null) {
                        Palette.generateAsync(bitmap, 32, new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                if (viewHolder.rootView.getTag() == contact) {
                                    viewHolder.vPalette.setBackgroundColor(palette.getVibrantColor(Color.GRAY));
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_grid_cell, parent, false);
        return new VH(itemView, getContext());
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
                        final Pair<View, String> p1 = Pair.create((View)ivProfile, "profile");
                        final Pair<View, String> p2 = Pair.create(vPalette, "palette");
                        final Pair<View, String> p3 = Pair.create((View)tvName, "text");
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, p1, p2, p3);
                        context.startActivity(intent, options.toBundle());
                    }
                }
            });
        }
    }
}
