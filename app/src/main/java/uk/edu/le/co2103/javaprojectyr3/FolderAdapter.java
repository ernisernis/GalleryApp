package uk.edu.le.co2103.javaprojectyr3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Folder> folders;

    public FolderAdapter(Context ct, ArrayList<Folder> folders) {
        this.context = ct;
        this.folders = folders;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView folderTitle;
        TextView folderCount;
        ImageView folderImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folderTitle = itemView.findViewById(R.id.folderTitle);
            folderCount = itemView.findViewById(R.id.folderCount);
            folderImage = itemView.findViewById(R.id.singleImageFolder);
        }
    }

    @NonNull
    @Override
    public FolderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_single_folder,parent,false);
        return new FolderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderAdapter.ViewHolder holder, int position) {
        Folder folder = folders.get(position);
        holder.folderTitle.setText(folder.getTitle());
        holder.folderCount.setText(String.valueOf(folder.getCount()));
        byte [] singleImage = folder.getImage();
        if (singleImage != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeByteArray(singleImage,0,singleImage.length,options);
            holder.folderImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap,200,250,false));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                holder.getAdapterPosition();
//                System.out.println(folders.get(holder.getAdapterPosition()).title);
                Intent intent = new Intent(context,ThirdActivity.class);
                intent.putExtra("FOLDERS_NAME", folders.get(holder.getAdapterPosition()).title);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return folders.size();
    }

}
