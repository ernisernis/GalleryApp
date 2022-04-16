package uk.edu.le.co2103.javaprojectyr3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import uk.edu.le.co2103.javaprojectyr3.DBHelper.DBHelper;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {

    private ArrayList<Bitmap> data1;
    private ArrayList<byte[]> data2;
    private Context context;
    private String dbPass;
    private String folderName;

    public RVAdapter(Context ct, ArrayList<Bitmap> images, String dbPass, String folderName, ArrayList<byte[]> data2) {
        this.context = ct;
        this.data1 = images;
        this.dbPass = dbPass;
        this.folderName = folderName;
        this.data2 = data2;
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

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                System.out.println("CLICKED LONG!!!!!!!!!");
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle("Delete this image");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DBHelper.getInstance(context).deleteImage(dbPass, (byte[]) data2.get(holder.getAdapterPosition()), folderName);
                        data1.remove(data1.get(holder.getAdapterPosition()));
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Toast.makeText(context, "No clicked", Toast.LENGTH_SHORT).show();

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data1.size();
    }


}
