package mc.apps.demo0.ui.clients;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import mc.apps.demo0.R;
import mc.apps.demo0.adapters.ClientsAdapter;
import mc.apps.demo0.dao.AdressDao;
import mc.apps.demo0.dao.ClientDao;
import mc.apps.demo0.dao.Dao;
import mc.apps.demo0.model.Adress;
import mc.apps.demo0.model.Client;
import mc.apps.demo0.viewmodels.MainViewModel;

public class ClientsFragments extends Fragment {
    private static final String TAG = "tests";
    private MainViewModel mainViewModel;
    public static ClientsFragments newInstance() {
        return new ClientsFragments();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layout = R.layout.fragment_clients;
        return inflater.inflate(layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        root = view;

        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        refreshListAsync();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainViewModel.getSearch().observe(
                getViewLifecycleOwner(),
                search -> {
                    if (search == null || search.length() == 0)
                        refreshListAsync();
                    else
                    if(adapter!=null)
                        adapter.getFilter().filter(search);
                }
        );
    }

    /**
     * Gestion liste..
     */
    List<Client> items = new ArrayList<Client>();
    List<Client> selected = new ArrayList<Client>();
    SwipeRefreshLayout swipeContainer;
    RecyclerView recyclerView, recyclerView_selected;

    ClientsAdapter adapter;
    //SelectedUsersAdapter adapter2;
    View root;
    private void loadList(){
        recyclerView = root.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ClientsAdapter(
                items,
                (position, item) -> {
                    //Toast.makeText(root.getContext(), "Selected : "+item.toString(), Toast.LENGTH_SHORT).show();
                    mainViewModel.setClient((Client) item);

                    /*Client client = (Client) item;
                    getClientAdress(client);*/
                }
        );
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                root.getContext(), R.anim.layout_fall_down_animation
        ));

        swipeContainer = root.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                refreshListAsync();
            }
        });
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright
        );
    }

    private void getClientAdress(Client client) {
        AdressDao dao = new AdressDao();
        dao.ofClient(client.getCode(), new Dao.OnSuccess() {
            @Override
            public void result(List<?> items, String message) {
                List<Adress> adresses = dao.Deserialize(items, Adress.class);
                for (Adress adress : adresses)
                   client.addAdress(adress);
            }
        });
    }

    private void refreshListAsync() {
        ClientDao dao = new ClientDao();
        dao.list((data, message) -> {
            items = dao.Deserialize(data, Client.class);

            /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                items = items.stream()
                        .sorted((o1, o2)->o1.getLastname().compareTo(o2.getLastname()))
                        .filter(u->u.getProfilId()>2)       //filtre techniciens
                        .collect(Collectors.toList());
            }*/

            // ajout adresses!
            for (Client client : items)
                getClientAdress(client);

            loadList();
            swipeContainer.setRefreshing(false);
        });
    }

}