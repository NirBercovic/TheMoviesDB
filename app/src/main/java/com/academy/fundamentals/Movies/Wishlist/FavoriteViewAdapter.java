package com.academy.fundamentals.Movies.Wishlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.academy.fundamentals.Movies.Movies.CommonData.Constants;
import com.academy.fundamentals.Movies.Movies.CommonData.ItemClickListener;
import com.academy.fundamentals.Movies.Movies.CommonData.MovieDetails;
import com.academy.fundamentals.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavoriteViewAdapter extends  RecyclerView.Adapter<FavoriteViewAdapter.ViewHolder> implements ItemClickListener, Constants {

    private LayoutInflater m_inflater;
    private ArrayList<MovieDetails> m_wishlist;
    private FavoriteActivity m_context;

    public FavoriteViewAdapter(FavoriteActivity context) {
        m_wishlist = Wishlist.getInstance(context).getWishlistMovies();
        m_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        m_context = context;
    }

    @Override
    public int getItemCount() {
        return m_wishlist.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = m_inflater.inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBindViewHolder(m_wishlist.get(position));
        holder.setItemClickListener(this);
    }

    @Override
    public void onClick(View view, int position) {
        m_context.startDetailsActivity(position);
    }

    @Override
    public void onLongClick(View view, int position) {
        MovieDetails movie = m_wishlist.get(position);
        Toast.makeText(m_context, movie.getName() + WISHLIST_REMOVE, Toast.LENGTH_SHORT).show();
        m_wishlist.remove(movie);
        notifyDataSetChanged();
        if (m_wishlist.isEmpty())
        {
            TextView text = m_context.findViewById(R.id.tv_empty_wishlist);
            text.setVisibility(View.VISIBLE);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        public final ImageView m_image;
        public final TextView m_tv_title;
        public final TextView m_tv_overview;
        private ItemClickListener itemClickListener;

        public ViewHolder(View view) {
            super(view);
            m_image = view.findViewById(R.id.iv_smallIcon);
            m_tv_title = view.findViewById(R.id.tv_nameHeadline);
            m_tv_overview = view.findViewById(R.id.tv_overviewText);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        public void onBindViewHolder(MovieDetails movie) {
            m_tv_title.setText(movie.getName());
            m_tv_overview.setText(movie.getOverview());
            if (!TextUtils.isEmpty(movie.getSidePosterPath())) {
                Picasso.get()
                        .load(movie.getSidePosterPath())
                        .into(m_image);
            }
        }

        public void setItemClickListener(ItemClickListener onClickCallback) {
            itemClickListener = onClickCallback;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onLongClick(v,getAdapterPosition());
            return true;
        }
    }
}