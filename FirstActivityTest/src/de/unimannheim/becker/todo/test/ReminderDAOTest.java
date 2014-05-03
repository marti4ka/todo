package de.unimannheim.becker.todo.test;

import android.test.ActivityInstrumentationTestCase2;
import de.unimannheim.becker.todo.md.CardsActivity;
import de.unimannheim.becker.todo.md.model.Item;
import de.unimannheim.becker.todo.md.model.ItemDAO;
import de.unimannheim.becker.todo.md.model.Reminder;
import de.unimannheim.becker.todo.md.model.ReminderDAO;

public class ReminderDAOTest extends ActivityInstrumentationTestCase2<CardsActivity> {

    public ReminderDAOTest() {
        super(CardsActivity.class);
    }

    private ReminderDAO sut;
    private ItemDAO itemDao;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sut = new ReminderDAO(getInstrumentation().getTargetContext());
        itemDao = new ItemDAO(getInstrumentation().getTargetContext());
        sut.deleteAll();
        itemDao.deleteAll();
    }
    
    public void testStoreItem() throws Exception {
        Item item = new Item();
        item.setTitle("aaa");
        item.setDescription("dddd");
        itemDao.storeItem(item);
        item = itemDao.getItems()[0];
        assertEquals(0, sut.getActive().length);
        Reminder reminder = new Reminder();
        double longtitude = 12.141231;
        double latitude = -123.23098123;
        reminder.setItemId(item.getId());
        reminder.setLongtitude(longtitude);
        reminder.setLatitude(latitude);
        boolean success = sut.storeReminder(reminder);
        assertTrue(success);
        Reminder[] reminders = sut.getActive();
        assertEquals(1, reminders.length);
        assertEquals(longtitude, reminders[0].getLongtitude());
        assertEquals(latitude, reminders[0].getLatitude());
    }


    @Override
    protected void tearDown() throws Exception {
        sut.deleteAll();
        itemDao.deleteAll();
        super.tearDown();
    }

}
