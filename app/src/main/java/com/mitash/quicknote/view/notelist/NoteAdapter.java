package com.mitash.quicknote.view.notelist;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mitash.quicknote.R;
import com.mitash.quicknote.database.converter.DateConverter;
import com.mitash.quicknote.database.entity.NoteEntity;
import com.mitash.quicknote.databinding.ItemNoteListBinding;
import com.mitash.quicknote.utils.TimeUtils;
import com.mitash.quicknote.view.stickyheader.StickyHeadersAdapter;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Mitash Gaurh on 9/7/2017.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.SectionViewHolder> implements StickyHeadersAdapter<RecyclerView.ViewHolder> {

    private List<NoteEntity> mNoteList;

    private NoteActionListener mNoteActionListener;

    public NoteAdapter(List<NoteEntity> noteList) {
        mNoteList = noteList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
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
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        textView.setText(TimeUtils.toMonthFormat(
                DateConverter.toTimestamp(mNoteList.get(position).getUpdatedDate())));
    }

    @Override
    public void onBindViewHolder(SectionViewHolder holder, int position) {
        holder.mBinding.setNote(mNoteList.get(position));
        holder.mBinding.executePendingBindings();
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

    public void setNoteActionListener(NoteActionListener actionListener) {
        mNoteActionListener = checkNotNull(actionListener);
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
                            && noteEntity.getUpdatedDate() == old.getUpdatedDate();
                }
            });
            mNoteList = noteList;
            result.dispatchUpdatesTo(this);
        }
    }

    class SectionViewHolder extends RecyclerView.ViewHolder {

        final ItemNoteListBinding mBinding;

        SectionViewHolder(ItemNoteListBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
