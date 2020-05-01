package com.smartloan.smtrick.smart_loan.view.fragement;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ramotion.foldingcell.FoldingCell;
import com.smartloan.smtrick.smart_loan.R;
import com.smartloan.smtrick.smart_loan.callback.CallBack;
import com.smartloan.smtrick.smart_loan.interfaces.OnFragmentInteractionListener;
import com.smartloan.smtrick.smart_loan.models.LeedsModel;
import com.smartloan.smtrick.smart_loan.preferences.AppSharedPreference;
import com.smartloan.smtrick.smart_loan.repository.LeedRepository;
import com.smartloan.smtrick.smart_loan.repository.impl.LeedRepositoryImpl;
import com.smartloan.smtrick.smart_loan.singleton.AppSingleton;
import com.smartloan.smtrick.smart_loan.utilities.Utility;
import com.smartloan.smtrick.smart_loan.view.adapter.LeedsAdapter;
import com.smartloan.smtrick.smart_loan.view.dialog.ProgressDialogClass;

import java.util.ArrayList;

public class LeedsFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    LeedRepository leedRepository;
    AppSingleton appSingleton;
    ProgressDialogClass progressDialogClass;
    AppSharedPreference appSharedPreference;
    ListView leedsListView;
    private LeedsAdapter adapter;
    Context context;
    private FloatingActionButton addNewLead;

    public LeedsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leads_layout, container, false);
        if (mListener != null) {
            mListener.onFragmentInteraction("Leads");
        }
        context = getActivity();
        progressDialogClass = new ProgressDialogClass(getActivity());
        appSingleton = AppSingleton.getInstance(getActivity());
        leedRepository = new LeedRepositoryImpl();
        appSharedPreference = new AppSharedPreference(getActivity());
        leedsListView = view.findViewById(R.id.mainListView);
        addNewLead = view.findViewById(R.id.fab_add_lead);
        addNewLeadClick();
        getteLeed();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            // NOTE: This is the part that usually gives you the error
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public LeedsAdapter getLeedAdapter() {
        return adapter;
    }

    private void getteLeed() {
        progressDialogClass.showDialog(this.getString(R.string.loading), this.getString(R.string.PLEASE_WAIT));
        leedRepository.readLeedsByUserId(getActivity(), appSharedPreference.getUserId(), new CallBack() {
            @Override
            public void onSuccess(Object object) {
                if (object != null) {
                    ArrayList<LeedsModel> leedsModelArrayList = (ArrayList<LeedsModel>) object;
                    serAdapter(leedsModelArrayList);
                }
                if (progressDialogClass != null)
                    progressDialogClass.dismissDialog();
            }

            @Override
            public void onError(Object object) {
                if (progressDialogClass != null)
                    progressDialogClass.dismissDialog();
                if (isAdded() && context != null)
                    Utility.showLongMessage(context, getString(R.string.server_error));
            }
        });
    }


    private void serAdapter(ArrayList<LeedsModel> leedsModels) {
        if (mListener != null) {
            mListener.onFragmentInteraction("Leads (" + leedsModels.size() + ")");
        }
        if (leedsModels != null) {
            if (adapter == null) {
                adapter = new LeedsAdapter(context, leedsModels);
                adapter.setDefaultRequestBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Toast.makeText(getApplicationContext(), "DEFAULT HANDLER FOR ALL BUTTONS", Toast.LENGTH_SHORT).show();
                    }
                });
                leedsListView.setAdapter(adapter);
                setItemClickListner();
            } else {
                ArrayList<LeedsModel> leedsModelArrayList = new ArrayList<>();
                leedsModelArrayList.addAll(leedsModels);
                adapter.reload(leedsModelArrayList);
            }
        }
    }

    private void setItemClickListner() {
        leedsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                ((FoldingCell) view).toggle(false);
                adapter.registerToggle(pos);
            }
        });
    }

    private void addNewLeadClick() {
        addNewLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.changeFragement(new GenerateLeedFragment());
            }
        });
    }
}
