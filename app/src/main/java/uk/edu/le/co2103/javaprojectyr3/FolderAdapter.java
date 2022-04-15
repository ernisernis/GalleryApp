package uk.edu.le.co2103.javaprojectyr3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uk.edu.le.co2103.javaprojectyr3.DBHelper.DBHelper;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {

    private static FolderAdapter instance;

    private Context context;
    private ArrayList<Folder> folders;
    private String dbPass;

    public FolderAdapter(Context ct, ArrayList<Folder> folders, String dbPass) {
        this.context = ct;
        this.folders = folders;
        this.dbPass = dbPass;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView folderTitle;
        TextView folderCount;
        ImageView folderImage;
        ImageView folderImage2;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folderTitle = itemView.findViewById(R.id.folderTitle);
            folderCount = itemView.findViewById(R.id.folderCount);
            folderImage = itemView.findViewById(R.id.singleImageFolder);
            folderImage2= itemView.findViewById(R.id.singleImageFolder2);
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
        String folderCount = "Photo count: " + folder.getCount();
        holder.folderCount.setText(folderCount);

        // Text font and color.
        Typeface type = Typeface.createFromAsset(context.getAssets(), "Cabin.ttf");
        Typeface typeItalic = Typeface.createFromAsset(context.getAssets(), "CabinItalic.ttf");
        holder.folderTitle.setTextColor(Color.parseColor("#fcfdfb"));
        holder.folderTitle.setTypeface(type);
        holder.folderCount.setTextColor(Color.parseColor("#a6a6a6"));
        holder.folderCount.setTypeface(typeItalic);


        byte [] singleImage = folder.getImage();
        if (singleImage != null) {
            holder.folderImage2.setVisibility(View.GONE);
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeByteArray(singleImage,0,singleImage.length,options);
            holder.folderImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap,240,250,false));
//            holder.folderImage.setImageBitmap(Bitmap.createBitmap(bitmap));
        } else {
            holder.folderImage.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                holder.getAdapterPosition();
//                System.out.println(folders.get(holder.getAdapterPosition()).title);
                Intent intent = new Intent(context,ThirdActivity.class);
                intent.putExtra("FOLDERS_NAME", folders.get(holder.getAdapterPosition()).title);
                intent.putExtra("PHOTO_COUNT", folder.getCount());
                ((Activity) context).startActivityForResult(intent, 1);
//                context.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle("Delete this folder");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "Yes clicked!" + holder.folderTitle.getText(), Toast.LENGTH_SHORT).show();
                        DBHelper.getInstance(context).deleteFolder(dbPass, (String) holder.folderTitle.getText());
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
        return folders.size();
    }

    public void clear() {
        int size = folders.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                folders.remove(0);
            }
            notifyItemRangeChanged(0,size);
        }
    }

    static public synchronized FolderAdapter getInstance(Context ct, ArrayList<Folder> folders, String dbPass){
        if (instance==null) instance = new FolderAdapter(ct, folders, dbPass);
        return instance;
    }

}
