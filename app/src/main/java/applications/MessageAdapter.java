package applications;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.bill.Activities.R;

import java.util.ArrayList;
import java.util.List;

// MessageAdapter.java
public class MessageAdapter extends BaseAdapter {

    List<Message> messages = new ArrayList<Message>();
    Context context;

    public MessageAdapter(Context context) {
        this.context = context;
    }


    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {

        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater =  (LayoutInflater)context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(position);

        if (message.isBelongsToCurrentUser()) {
            convertView = messageInflater.inflate(R.layout.my_message, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getText());
        } else {
            convertView = messageInflater.inflate(R.layout.their_message, null);
            holder.avatar = (View) convertView.findViewById(R.id.avatar);

            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            holder.messageBody.setText(message.getText());

            GradientDrawable drawable = (GradientDrawable) holder.avatar.getBackground();
            drawable.setColor(Color.parseColor("#8F7762"));

            convertView.setTag(holder);
        }

        return convertView;
    }

}

class MessageViewHolder {
    public View avatar;
    public TextView name;
    public TextView messageBody;
}