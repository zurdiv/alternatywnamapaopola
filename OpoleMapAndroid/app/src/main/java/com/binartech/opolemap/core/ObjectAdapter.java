package com.binartech.opolemap.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public final class ObjectAdapter<T> extends BaseAdapter
{
    public interface ViewRenderer<T>
    {
        /**
         * Called when there is a need to render particular item
         * @param item
         * @param pos
         * @param convertible
         * @param parent
         * @return
         */
        public View renderView(T item, int pos, View view, ViewGroup parent);

    }

    public interface ViewSelector<T> extends ViewRenderer<T>
    {
        /**
         * Called when implemetation wants to determine type of selected view
         * @return
         */
        public int getRenderedViewsTypeCount();

        /**
         * Called to determine type of view for selected position
         * @param position
         * @return
         */
        public int getRenderedViewType(T item, int position);

        /**
         * Called to determine if selected items are clickable and selectable
         * @return
         */
        public boolean areAllItemsEnabled();

        public boolean isRenderedViewClickable(T item, int position);
    }

    /**
     * List of backed objects
     */
    private final ArrayList<T> mList = new ArrayList<T>();
    /**
     * Renderer
     */
    private final ViewRenderer<T> mRenderer;
    /**
     * View details selector
     */
    private final ViewSelector<T> mSelector;

    /**
     * Default setting
     */
    private final boolean mAreAllItemsEnabled;

    public List<T> getItems()
    {
        return mList;
    }

    public ObjectAdapter(ViewRenderer<T> renderer)
    {
        this(renderer, true);
    }

    public ObjectAdapter(ViewRenderer<T> renderer, boolean isListClickable)
    {
        if (renderer != null)
        {
            mRenderer = renderer;
            mSelector = (renderer instanceof ViewSelector<?>) ? (ViewSelector<T>) renderer : null;
            mAreAllItemsEnabled = isListClickable;
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int getCount()
    {
        return mList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mList.get(position);
    }

    public T getItemAt(int pos)
    {
        return mList.get(pos);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return mRenderer.renderView(mList.get(position), position, convertView, parent);
    }

    /**
     * Add range of objects to the adapter
     * @param collection
     */
    public synchronized void addRange(Collection<T> collection)
    {
        mList.addAll(collection);
        notifyDataSetChanged();
    }

    public synchronized void addElement(T element)
    {
        mList.add(element);
        notifyDataSetChanged();
    }

    /**
     * Sets range of object as base adapter collection
     * @param collection
     */
    public synchronized void setRange(Collection<T> collection)
    {
        mList.clear();
        mList.addAll(collection);
        notifyDataSetChanged();
    }

    /**
     * Clears adapter data
     */
    public synchronized void clear()
    {
        mList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position)
    {
        if (mSelector != null)
        {
            return mSelector.getRenderedViewType(mList.get(position), position);
        }
        else
        {
            return 0;
        }
    }

    @Override
    public int getViewTypeCount()
    {
        if (mSelector != null)
        {
            return mSelector.getRenderedViewsTypeCount();
        }
        else
        {
            return 1;
        }
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        if (mSelector != null)
        {
            return mSelector.areAllItemsEnabled();
        }
        else
        {
            return mAreAllItemsEnabled;
        }
    }

    @Override
    public boolean isEnabled(int position)
    {
        if (mSelector != null)
        {
            return mSelector.isRenderedViewClickable(mList.get(position), position);
        }
        else
        {
            return mAreAllItemsEnabled;
        }
    }

    public synchronized void updateList()
    {
        notifyDataSetChanged();
    }

}
