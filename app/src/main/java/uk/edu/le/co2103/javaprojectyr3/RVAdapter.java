package uk.edu.le.co2103.javaprojectyr3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {

    private ArrayList<String> data1 ;
    private Context context;

    public RVAdapter(Context ct, ArrayList<String> images) {
        context = ct;
        data1 = images;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView myImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myImage = itemView.findViewById(R.id.myImageView);
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
//        holder.myImage.setImageBitmap();
        String current = data1.get(position).replace("[","");
        current = current.replace("]","");

        String[] bytesString = current.split(", ");
        byte[] bytes = new byte[bytesString.length];
        for(int i = 0 ; i < bytes.length ; ++i) {
            bytes[i] = Byte.parseByte(bytesString[i]);
        }
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        holder.myImage.setImageBitmap(Bitmap.createScaledBitmap(bmp,800,800,false));
    }

    @Override
    public int getItemCount() {
        return data1.size();
    }
}
