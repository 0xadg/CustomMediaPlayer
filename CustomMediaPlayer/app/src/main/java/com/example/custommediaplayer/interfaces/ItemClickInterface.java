package com.example.custommediaplayer.interfaces;

import android.view.ContextMenu;
import android.view.View;

public interface ItemClickInterface {
    void onItemClick(int position, ItemType type);
    void onItemContextMenuCreation(int position, ItemType type, ContextMenu menu, View v, ContextMenu.ContextMenuInfo info);
}

