package com.mitash.quicknote.view.notelist;

import android.graphics.Typeface;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mitash.quicknote.database.entity.NoteEntity;
import com.mitash.quicknote.databinding.ItemNoteHeaderBinding;
import com.mitash.quicknote.databinding.ItemNoteListBinding;
import com.mitash.quicknote.view.stickyheader.StickyHeadersAdapter;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Mitash Gaurh on 9/7/2017.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.SectionViewHolder> implements StickyHeadersAdapter<NoteAdapter.HeaderViewHolder> {

    private List<NoteEntity> mNoteList;

    private NoteActionListener mNoteActionListener;

    NoteAdapter(List<NoteEntity> noteList) {
        mNoteList = noteList;
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new HeaderViewHolder(ItemNoteHeaderBinding.inflate(LayoutInflater.from(parent.getContext())
                , parent
                , false));
    }

    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemNoteListBinding binding = ItemNoteListBinding.inflate(LayoutInflater.from(parent.getContext())
                , parent
                , false);
        binding.setListener(mNoteActionListener);
        return new SectionViewHolder(binding);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder holder, int position) {
        holder.mItemNoteHeaderBinding.setNote(mNoteList.get(position));
        holder.mItemNoteHeaderBinding.executePendingBindings();
    }

    @Override
    public void onBindViewHolder(SectionViewHolder holder, int position) {
        holder.mItemNoteListBinding.setNote(mNoteList.get(position));
        holder.mItemNoteListBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return null != mNoteList ? mNoteList.size() : 0;
    }

    @Override
    public long getHeaderId(int position) {
        if (position < 0) {
            return -1;
        } else {
            return getItem(position);
        }
    }

    private long getItem(int position) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mNoteList.get(position).getUpdatedDate());
        return calendar.get(Calendar.MONTH);
    }

    NoteEntity getNote(int position) {
        return mNoteList.get(position);
    }

    void setNoteActionListener(NoteActionListener actionListener) {
        mNoteActionListener = checkNotNull(actionListener);
    }

    void removeItem(int position) {
        mNoteList.remove(position);
        notifyItemRemoved(position);
    }

    void restoreItem(NoteEntity item, int position) {
        mNoteList.add(position, item);
        notifyItemInserted(position);
    }

    void swapNoteList(final List<NoteEntity> noteList) {
        if (null == mNoteList) {
            mNoteList = noteList;
            notifyItemRangeInserted(0, noteList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mNoteList.size();
                }

                @Override
                public int getNewListSize() {
                    return noteList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mNoteList.get(oldItemPosition).getId() ==
                            noteList.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    NoteEntity noteEntity = noteList.get(newItemPosition);
                    NoteEntity old = noteList.get(oldItemPosition);
                    return noteEntity.getId() == old.getId()
                            && Objects.equals(noteEntity.getTitle(), old.getTitle())
                            && Objects.equals(noteEntity.getNoteText(), old.getNoteText())
                            && noteEntity.getUpdatedDate() == old.getUpdatedDate();
                }
            });
            mNoteList = noteList;
            result.dispatchUpdatesTo(this);
        }
    }

    public class SectionViewHolder extends RecyclerView.ViewHolder {

        public final ItemNoteListBinding mItemNoteListBinding;

        SectionViewHolder(ItemNoteListBinding binding) {
            super(binding.getRoot());
            mItemNoteListBinding = binding;
            mItemNoteListBinding.tvItemNoteTitle.setTypeface(
                    Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Merriweather-Regular.ttf"));
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        final ItemNoteHeaderBinding mItemNoteHeaderBinding;

        HeaderViewHolder(ItemNoteHeaderBinding binding) {
            super(binding.getRoot());
            mItemNoteHeaderBinding = binding;
        }
    }
}
