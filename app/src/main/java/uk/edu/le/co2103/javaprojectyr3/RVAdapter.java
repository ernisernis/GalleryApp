package uk.edu.le.co2103.javaprojectyr3;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uk.edu.le.co2103.javaprojectyr3.DBHelper.DBHelper;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {

    private ArrayList<Bitmap> data1;
    private ArrayList<byte[]> data2;
    private Context context;
    private String dbPass;
    private String folderName;
    private int folderNumber;

    public RVAdapter(Context ct, ArrayList<Bitmap> images, String dbPass, String folderName, ArrayList<byte[]> data2, int folderNumber) {
        this.context = ct;
        this.data1 = images;
        this.dbPass = dbPass;
        this.folderName = folderName;
        this.data2 = data2;
        this.folderNumber = folderNumber;
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

        holder.itemView.setOnLongClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(true);
            builder.setTitle("Delete this image");
            builder.setMessage("Are you sure?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                if (holder.getAdapterPosition() == 0) {
                    ((IndividualFolderActivity)context).changeFolderImageBubble();
                }
                DBHelper.getInstance(context).deleteImage(dbPass, data2.get(holder.getAdapterPosition()), folderName);
                data1.remove(data1.get(holder.getAdapterPosition()));
                folderNumber = folderNumber - 1;
                ((IndividualFolderActivity)context).putCount(folderNumber);
                ((IndividualFolderActivity)context).changeActivityStatus();
                notifyItemRemoved(holder.getAdapterPosition());
            });
            builder.setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> Toast.makeText(context, "Button cancelled", Toast.LENGTH_SHORT).show());
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return data1.size();
    }


}
