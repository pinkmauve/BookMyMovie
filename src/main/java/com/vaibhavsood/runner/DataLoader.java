package com.vaibhavsood.runner;

import com.vaibhavsood.data.entity.Movie;
import com.vaibhavsood.data.entity.Screen;
import com.vaibhavsood.data.entity.Screening;
import com.vaibhavsood.data.repository.MovieRepository;
import com.vaibhavsood.data.repository.ScreenRepository;
import com.vaibhavsood.data.repository.ScreeningRepository;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataLoader implements ApplicationRunner {
    private MovieRepository movieRepository;
    private ScreenRepository screenRepository;
    private ScreeningRepository screeningRepository;

    public MovieRepository getMovieRepository() {
        return movieRepository;
    }

    public ScreeningRepository getScreeningRepository() {
        return screeningRepository;
    }

    public ScreenRepository getScreenRepository() {
        return screenRepository;
    }

    @Autowired
    public DataLoader(MovieRepository movieRepository, ScreeningRepository screeningRepository,
                      ScreenRepository screenRepository) {
        this.movieRepository = movieRepository;
        this.screeningRepository = screeningRepository;
        this.screenRepository = screenRepository;
    }

    private void populateMovieTable() {
        try (BufferedReader brMovies = new BufferedReader(new FileReader("C:\\Users\\vaibhav_sood.PERSISTENT\\Downloads\\Ex_Files_Learning_Spring_Boot\\Ex_Files_Learning_Spring_Boot\\Exercise Files\\Chapter 1\\01_03\\01_03_end\\reservations\\src\\main\\resources\\movies.csv"));
             BufferedReader brLinks = new BufferedReader(new FileReader("C:\\Users\\vaibhav_sood.PERSISTENT\\Downloads\\Ex_Files_Learning_Spring_Boot\\Ex_Files_Learning_Spring_Boot\\Exercise Files\\Chapter 1\\01_03\\01_03_end\\reservations\\src\\main\\resources\\links.csv"))) {
            String line;
            brMovies.readLine();    // Skip header line
            brLinks.readLine();     // Skip header line
            while ((line = brMovies.readLine()) != null) {
                String[] movieLine = line.split(",");

                String movieName = "";

                for (int i = 1; i < movieLine.length-1; i++) {
                    if (i == movieLine.length-2)
                        movieName += movieLine[i];
                    else
                        movieName += movieLine[i] + ",";
                }

                Movie movie = new Movie();
                movie.setMovieId(Long.parseLong(movieLine[0]));
                movie.setMovieName(movieName.substring(0, movieName.indexOf('(')).trim());

                String line1 = brLinks.readLine();
                String[] linkLine = line1.split(",");
                Document movieLensPage;
                try {
                    movieLensPage = Jsoup.connect("https://www.imdb.com/title/tt" + linkLine[1]).get();
                } catch (HttpStatusException e) {
                    continue;
                }
                Element image = movieLensPage.getElementsByClass("poster").first().children().first().children().first();
                movie.setMoviePosterUrl(image.attr("src"));

                movieRepository.save(movie);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateScreeningsTable() {
        /* schema.sql lists 5 theaters, generate 2 screenings randomly for
         * each screen in each theater
         */
        for (int i = 1; i <= 5; i++) {
            List<Screen> screens = screenRepository.findByTheatreId(i);
            for (int j = 0; j < screens.size(); j++) {
                Screening screening1 = new Screening();
                Screening screening2 = new Screening();

                screening1.setTheatreId(i);
                screening1.setScreenId(j+1);
                screening2.setTheatreId(i);
                screening2.setScreenId(j+1);

                // Randomly select 2 movies from the movies db, 1 each for each screen
                long totalMovies = movieRepository.count();
                Random random = new Random();
                long movieId1 = random.nextInt((int)totalMovies)+1;
                long movieId2 = random.nextInt((int)totalMovies)+1;     // TODO: check movieId1 != movieId2
                Movie movie1 = movieRepository.findByMovieId(movieId1);
                Movie movie2 = movieRepository.findByMovieId(movieId2);

                screening1.setMovieName(movie1.getMovieName());
                screening2.setMovieName(movie2.getMovieName());

                // Get a random date between current date and 3 days from current date
                Date date1 = new Date((new java.util.Date()).getTime());
                Date date2 = new Date(date1.getTime()+3*24*60*60*1000);
                Date randomDate1 = new Date(ThreadLocalRandom.current().nextLong(date1.getTime(), date2.getTime()));
                Date randomDate2 = new Date(ThreadLocalRandom.current().nextLong(date1.getTime(), date2.getTime()));

                screening1.setScreeningDate(randomDate1);
                screening2.setScreeningDate(randomDate2);

                screening1.setBookedTickets(0);
                screening2.setBookedTickets(0);

                // 2 screenings per screen
                screening1.setScreeningTime(Time.valueOf("10:00:00"));
                screeningRepository.save(screening1);
                screening1.setScreeningTime(Time.valueOf("18:00:00"));
                screeningRepository.save(screening1);

                if (randomDate1.getDate() != randomDate2.getDate()) {
                    screening2.setScreeningTime(Time.valueOf("10:00:00"));
                    screeningRepository.save(screening2);
                    screening2.setScreeningTime(Time.valueOf("18:00:00"));
                    screeningRepository.save(screening2);
                }
            }
        }
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        populateMovieTable();
        populateScreeningsTable();
    }
}