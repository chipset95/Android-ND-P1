package chipset.pone.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import chipset.pone.R;
import chipset.pone.fragments.DefaultFragment;
import chipset.pone.fragments.MoviesFragment;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.movies_frame, new MoviesFragment()).commit();

        if (getResources().getBoolean(R.bool.is_tablet))
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_frame, new DefaultFragment()).commit();

    }
}

