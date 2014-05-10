package de.unimannheim.becker.todo.test;

import android.test.ActivityInstrumentationTestCase2;
import de.unimannheim.becker.todo.md.CardsActivity;
import de.unimannheim.becker.todo.md.model.Item;
import de.unimannheim.becker.todo.md.model.ItemDAO;

public class ItemDAOTest extends ActivityInstrumentationTestCase2<CardsActivity> {

	public ItemDAOTest() {
		super(CardsActivity.class);
	}

	private ItemDAO sut;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sut = new ItemDAO(getInstrumentation().getTargetContext());
		sut.deleteAll();
	}

	public void testStoreItem() throws Exception {
		assertEquals(0, sut.getItems().length);

		Item item = new Item();
		item.setTitle("titleTest");
		item.setDescription("descrTest");
		long id = item.getId();
		assertTrue(sut.storeItem(item));
		long id2 = item.getId();
		assertTrue(id != id2);
		Item[] items = sut.getItems();
		assertEquals(1, items.length);
		assertEquals(item.getTitle(), items[0].getTitle());
		assertEquals(item.getDescription(), items[0].getDescription());
		sut.storeItem(item);
		assertTrue(id2 != item.getId());
		assertTrue(id != item.getId());
	}
	
	public void testArchiveItem() throws Exception {
        Item item = new Item();
        item.setTitle("titleTest");
        item.setDescription("descrTest");
        boolean success = sut.storeItem(item);
        assertTrue(success);
        Item[] items = sut.getItems();
        assertEquals(1, items.length);
        boolean res = sut.archiveItem(items[0].getId());
        assertTrue(res);
        items = sut.getItems();
        Item[] archived = sut.getArchived();
        assertEquals(1, archived.length);
        assertEquals(0, items.length);
    }

	@Override
	protected void tearDown() throws Exception {
		sut.deleteAll();
		super.tearDown();
	}
}
