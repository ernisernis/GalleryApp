package uk.edu.le.co2103.javaprojectyr3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folderTitle = itemView.findViewById(R.id.folderTitle);
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
    }


    @Override
    public int getItemCount() {
        return folders.size();
    }

}