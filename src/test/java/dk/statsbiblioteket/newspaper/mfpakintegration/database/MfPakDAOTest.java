package dk.statsbiblioteket.newspaper.mfpakintegration.database;

import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Batch;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Event;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 9/18/13
 * Time: 1:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class MfPakDAOTest {

    private MfPakConfiguration configuration;

    @BeforeMethod(groups = {"integrationTest"})
    public void loadConfiguration() throws IOException {
        ConfigurationProvider configurationProvider = new ConfigurationProvider();
        configuration = configurationProvider.loadConfiguration();
    }

    /**
     * Test that we can get batches and events out of the database via the DAO. There should be at least fpour batches
     * in the database and at least one event each of types "Shipping" and "Received".
     * @throws Exception
     */
    @Test(groups = {"integrationTest"})
    public void testGetAllBatches() throws Exception {
        MfPakDAO dao = new MfPakDAO(configuration);
        List<Batch> batches = dao.getAllBatches();
        assertTrue("Should have at least four batches", batches.size() > 3);
        int shipped = 0;
        int created = 0;
        for (Batch batch: batches) {
            for (Event event: batch.getEventList() ) {
                if (event.getEventID().equals("Shipped")) {
                    shipped++;
                } else if (event.getEventID().equals("Created")) {
                    created++;
                } else {
                    throw new RuntimeException("Unknown event type " + event.getEventID());
                }
            }
        }
        assertTrue("Should have at least one Shipping event", shipped > 0);
        assertTrue("Should have at least one Received event", created > 0);
    }

    @Test(groups = {"integrationTest"})
    public void testGetBatchByBarcode() throws SQLException {
        MfPakDAO dao = new MfPakDAO(configuration);
        Batch batch = dao.getBatchByBarcode(4004);
        assertNotNull("Should get non-null batch", batch);
        assertEquals("Batch should have two events.", 2, batch.getEventList().size());
    }

    @Test(groups = {"integrationTest"})
    public void testGetEvent() throws SQLException {
        MfPakDAO dao = new MfPakDAO(configuration);
        Event event = dao.getEvent(4002, "Created");
        assertNotNull("Should have found this event.", event);
    }
}