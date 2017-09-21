package com.mitash.quicknote.view.adapter;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mitash.quicknote.database.entity.NoteEntity;
import com.mitash.quicknote.databinding.ItemNoteListBinding;

import java.util.List;
import java.util.Objects;

/**
 * Created by Mitash Gaurh on 9/7/2017.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private List<NoteEntity> mNoteList;

    public NoteAdapter(List<NoteEntity> noteList) {
        mNoteList = noteList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemNoteListBinding.inflate(LayoutInflater.from(parent.getContext())
                , parent
                , false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mBinding.setNote(mNoteList.get(position));
        holder.mBinding.executePendingBindings();
    }

    public void swapNoteList(final List<NoteEntity> noteList) {
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
                            && noteEntity.getCreatedDate() == old.getCreatedDate();
                }
            });
            mNoteList = noteList;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public int getItemCount() {
        return null != mNoteList ? mNoteList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final ItemNoteListBinding mBinding;

        ViewHolder(ItemNoteListBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
