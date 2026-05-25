package packagee.core.hospital;

import java.util.ArrayList;
import java.util.Optional;

public interface HospitalizationRepository {
    void addHospitalization(Hospitalization hospitalization);
    Optional<Hospitalization> findHospitalizationID(String id);
    ArrayList<Hospitalization> getHospitalizations();
}

class InMemoryHospitalizationRepository implements HospitalizationRepository {

    private final ArrayList<Hospitalization> hospitalizations = new ArrayList<>();

    @Override
    public void addHospitalization(Hospitalization hospitalization) {
        hospitalizations.add(hospitalization);
    }

    @Override
    public Optional<Hospitalization> findHospitalizationID(String id) {
        return hospitalizations.stream()
                .filter(h -> h.getId().equals(id))
                .findFirst();
    }

    @Override
    public ArrayList<Hospitalization> getHospitalizations() {
        return hospitalizations;
    }
}
