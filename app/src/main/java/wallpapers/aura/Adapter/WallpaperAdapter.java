package wallpapers.aura.Adapter;

import android.animation.Animator;
import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.squareup.picasso.Picasso;

import java.util.List;

import wallpapers.aura.Data.WallpaperInfo;
import wallpapers.aura.R;


public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder> {

    List<WallpaperInfo> list;
    Context context;

    public WallpaperAdapter(Context context, List<WallpaperInfo> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public WallpaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.walls_recycler, parent, false);
        return new WallpaperViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final WallpaperViewHolder holder, int position) {
        final WallpaperInfo wallinfo = list.get(position);
        wallinfo.sharedElement = holder.wallImageHolder;
        String name = wallinfo.walltitle;
        name = name.replace("_", " ");
        holder.wallTitleHolder.setText(name);

        String author = wallinfo.author;
        author = author.replace("_", " ");
        author = "| " + author;
        holder.wallAuthorHolder.setText(author);

        holder.wallImageHolder.setVisibility(View.INVISIBLE);

        Picasso.with(context)
                .load(wallinfo.wallthumb)
                .fit()
                .centerCrop()
                .into(holder.wallImageHolder, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                // get the center for the clipping circle
                                final int cx = (int) ((holder.wallImageHolder.getLeft() + holder.wallImageHolder.getRight()) * Math.random());
                                final int cy = (int) ((holder.wallImageHolder.getTop() + holder.wallImageHolder.getBottom()) * Math.random());

                                // get the final radius for the clipping circle
                                int dx = Math.max(cx, holder.wallImageHolder.getWidth() - cx);
                                int dy = Math.max(cy, holder.wallImageHolder.getHeight() - cy);
                                final float finalRadius = (float) Math.hypot(dx, dy);

                                // Android native animator
                                if (android.os.Build.VERSION.SDK_INT >= 21) {
                                    holder.wallImageHolder.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (holder.wallImageHolder.isAttachedToWindow()) {
                                                Animator animator = ViewAnimationUtils.createCircularReveal(holder.wallImageHolder, cx, cy, 0, finalRadius);
                                                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                                                animator.setDuration(850);
                                                animator.start();
                                                holder.wallImageHolder.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                                } else {
                                    holder.wallImageHolder.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onError() {
                                Log.e("WallHolder", "Name = " + wallinfo.walltitle + " Exception");
                            }
                        }
                );

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class WallpaperViewHolder extends RecyclerView.ViewHolder {

        TextView wallTitleHolder, wallAuthorHolder;
        KenBurnsView wallImageHolder;
        RelativeLayout relativelayout;
        CardView card;

        public WallpaperViewHolder(View itemView) {
            super(itemView);
            wallImageHolder = (KenBurnsView) itemView.findViewById(R.id.wallimagerecycle);
            wallAuthorHolder = (TextView) itemView.findViewById(R.id.wallauthorrecycle);
            wallTitleHolder = (TextView) itemView.findViewById(R.id.walltitlerecycle);
            relativelayout = (RelativeLayout) itemView.findViewById(R.id.linearwalldownload);
            card = (CardView) itemView.findViewById(R.id.card);
        }

    }
}