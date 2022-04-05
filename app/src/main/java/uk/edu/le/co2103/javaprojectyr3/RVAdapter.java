package uk.edu.le.co2103.javaprojectyr3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {

    private ArrayList<Bitmap> data1 ;
    private Context context;

    public RVAdapter(Context ct, ArrayList<Bitmap> images) {
        this.context = ct;
        this.data1 = images;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView myImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myImage = itemView.findViewById(R.id.myImageView);
            myImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }


    @NonNull
    @Override
    public RVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_single_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVAdapter.MyViewHolder holder, int position) {

        Bitmap bmp = data1.get(position);
        holder.myImage.setImageBitmap(Bitmap.createScaledBitmap(bmp,200,250,false));
//        holder.myImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context, "Item clicked!" + holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return data1.size();
    }


}
