package de.unimannheim.becker.todo.test;

import android.test.ActivityInstrumentationTestCase2;
import de.unimannheim.becker.todo.md.CardsActivity;
import de.unimannheim.becker.todo.md.model.Item;
import de.unimannheim.becker.todo.md.model.ItemDAO;
import de.unimannheim.becker.todo.md.model.Location;
import de.unimannheim.becker.todo.md.model.LocationDAO;

public class ReminderDAOTest extends ActivityInstrumentationTestCase2<CardsActivity> {

    public ReminderDAOTest() {
        super(CardsActivity.class);
    }

    private LocationDAO sut;
    private ItemDAO itemDao;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sut = new LocationDAO(getInstrumentation().getTargetContext());
        itemDao = new ItemDAO(getInstrumentation().getTargetContext());
        sut.deleteAll();
    }

    public void testStoreItem() throws Exception {
        assertEquals(0, sut.getAll().length);
        Location reminder = new Location();
        double longtitude = 12.141231;
        double latitude = -123.23098123;
        reminder.setLongtitude(longtitude);
        reminder.setLatitude(latitude);
        long id = reminder.getId();
        assertTrue(sut.storeLocation(reminder));
        long id2 = reminder.getId();
        assertFalse(id2 == id);
        Location[] reminders = sut.getAll();
        assertEquals(1, reminders.length);
        assertEquals(longtitude, reminders[0].getLongtitude());
        assertEquals(latitude, reminders[0].getLatitude());
        //check autoincrement id
        assertTrue(sut.storeLocation(reminder));
        long id3 = reminder.getId();
        assertFalse(id == id2);
        assertFalse(id2 == id3);
    }

    public void testMapLocationToItem() throws Exception {
        Item item = new Item("titleTest", "descrTest");
        Location location = new Location(123.123123, 3453.123123);
        sut.storeLocation(location);
        itemDao.storeItem(item);
        Location[] locationsForItem = sut.getLocationsForItem(item.getId());
        assertEquals(0, locationsForItem.length);
        boolean success = sut.mapLocationToItem(location.getId(), item.getId());
        assertTrue(success);
        locationsForItem = sut.getLocationsForItem(item.getId());
        assertEquals(1, locationsForItem.length);
        assertEquals(location, locationsForItem[0]);
    }
    
    @Override
    protected void tearDown() throws Exception {
        sut.deleteAll();
        super.tearDown();
    }

}
