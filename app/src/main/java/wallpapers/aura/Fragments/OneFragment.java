package wallpapers.aura.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wallpapers.aura.Adapter.WallpaperAdapter;
import wallpapers.aura.Data.WallpaperInfo;
import wallpapers.aura.R;
import wallpapers.aura.Singleton.SingletonInfo;
import wallpapers.aura.Util.Basic;
import wallpapers.aura.WallpaperActivity;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class OneFragment extends Fragment {

    String json_link;
    Context context;
    List<WallpaperInfo> list;
    RecyclerView.Adapter mWallpaperAdapter;
    RecyclerView mRecyclerView;
    CardView noInternetCard;
    Boolean isCategory;

    public static OneFragment newInstance(String json, Boolean category) {
        OneFragment oneFragment = new OneFragment();
        Bundle args = new Bundle();
        args.putString("JsonLink", json);
        args.putBoolean("isCategory", category);
        oneFragment.setArguments(args);
        return oneFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler, container, false);
    }

    public void refreshLayout() {
        list = new ArrayList<>();
        mWallpaperAdapter = null;
        mRecyclerView.setAdapter(null);
        makeWallpaperListReady();
        for (int i = 0; i < list.size(); i++) {
            Log.e("List", list.get(i).walltitle);
        }
        Collections.shuffle(list);
        for (int i = 0; i < list.size(); i++) {
            Log.e("List Changed", list.get(i).walltitle);
        }
        mWallpaperAdapter = new WallpaperAdapter(context, list);
        mRecyclerView.setAdapter(mWallpaperAdapter);
        if (Basic.isNetworkAvailable(context)) {
            noInternetCard.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noInternetCard.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);

        json_link = getArguments().getString("JsonLink");
        isCategory = getArguments().getBoolean("isCategory");

        context = getContext();
        list = new ArrayList<>();
        mWallpaperAdapter = new WallpaperAdapter(getContext(), list);
        makeWallpaperListReady();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.setHasFixedSize(true);

        if (isCategory) {
            mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        }

        noInternetCard = (CardView) view.findViewById(R.id.nointernet);

        if (Basic.isNetworkAvailable(getContext())) {
            noInternetCard.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noInternetCard.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        //mAdapter = new RecyclerViewMaterialAdapter();
        mRecyclerView.setAdapter(mWallpaperAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getContext(),
                    new GestureDetector.OnGestureListener() {
                        @Override
                        public boolean onDown(MotionEvent motionEvent) {
                            return false;
                        }

                        @Override
                        public void onShowPress(MotionEvent motionEvent) {

                        }

                        @Override
                        public boolean onSingleTapUp(MotionEvent motionEvent) {
                            return true;
                        }

                        @Override
                        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                            return false;
                        }

                        @Override
                        public void onLongPress(MotionEvent motionEvent) {

                        }

                        @Override
                        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                            return false;
                        }
                    });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    // RecyclerView Clicked item value
                    int position = rv.getChildAdapterPosition(child);
                    WallpaperInfo theme = list.get(position);
                    Intent i = new Intent(getActivity(), WallpaperActivity.class);

                    i.putExtra("wall_image", theme.walllink);
                    i.putExtra("wall_name", theme.walltitle);
                    i.putExtra("wall_author", theme.author);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_up, R.anim.stay);
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void makeWallpaperListReady() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET
                , json_link
                , null
                , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject wallpaper = (JSONObject) response
                                .get(i);
                        if (wallpaper.has("thumb_url")) {
                            list.add(new WallpaperInfo(wallpaper.getString("url")
                                    , wallpaper.getString("name")
                                    , wallpaper.getString("thumb_url")
                                    , wallpaper.getString("author")
                            ));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mWallpaperAdapter.notifyDataSetChanged();
            }
        }

                , new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response Error", error.toString());
            }
        }

        );
        SingletonInfo.getInstance().addToRequestQueue(jsonArrayRequest);
    }
}
