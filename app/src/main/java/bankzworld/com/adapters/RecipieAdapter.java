package bankzworld.com.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import bankzworld.com.R;
import bankzworld.com.pojo.Receipie;


/**
 * Created by Jaycee on 6/17/2017.
 */

public class RecipieAdapter extends RecyclerView.Adapter<RecipieAdapter.RecipieViewHolder>{

    final private ListItemClickListener mOnClickListener;
    final private ArrayList<Receipie> receipie;

    public RecipieAdapter(ListItemClickListener listener, ArrayList<Receipie> receipie) {
        mOnClickListener = listener;
        this.receipie = receipie;
    }


    @Override
    public RecipieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.card_item;

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        RecipieViewHolder viewHolder = new RecipieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecipieViewHolder holder, int position) {

        holder.onBind(position);

    }

    @Override
    public int getItemCount() {
        return receipie.size();
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    class RecipieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView icon;
        TextView name;
        TextView servings;


        public RecipieViewHolder(View itemView) {
            super(itemView);

            icon = (ImageView) itemView.findViewById(R.id.recipes_image);
            name = (TextView) itemView.findViewById(R.id.recipes_name);
            servings = (TextView) itemView.findViewById(R.id.recipes_servings);
            itemView.setOnClickListener(this);
        }

        void onBind(int position) {
            if (!receipie.isEmpty()) {
                if(receipie.get(position).getImage().isEmpty()){
                    icon.setImageResource(R.drawable.no_image);


                }else {
                    Picasso.with(itemView.getContext()).load(receipie.get(position).getImage()).into(icon);
                }
                name.setText(receipie.get(position).getName());
                servings.setText(itemView.getContext().getString(R.string.servings) + " " + receipie.get(position).getServings());
            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

}
