package packagee.core.hospital;

import packagee.core.person.Doctor;
import packagee.core.person.User;
import java.util.ArrayList;
import java.util.Optional;

public interface UserRepository {
    void addUser(User user);
    Optional<User> findUser(String username);
    Optional<User> findID(long id);
    ArrayList<User> getUsers();
    ArrayList<Doctor> getDoctors();
}

class InMemoryUserRepository implements UserRepository {

    private final ArrayList<User> users;

    public InMemoryUserRepository(ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public void addUser(User user) {
        users.add(user);
    }

    @Override
    public Optional<User> findUser(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public Optional<User> findID(long id) {
        return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst();
    }

    @Override
    public ArrayList<User> getUsers() {
        return users;
    }

    @Override
    public ArrayList<Doctor> getDoctors() {
        ArrayList<Doctor> doctors = new ArrayList<>();
        for (User u : users) {
            if (u instanceof Doctor) {
                doctors.add((Doctor) u);
            }
        }
        return doctors;
    }
}
