// Mock data for RetailOS UI
export const mockProducts = [
  { id: '1', name: 'Parle-G Biscuit', sku: 'PLG-100', category: 'Snacks', price: 10, mrp: 10, stock: 120, unit: 'pkt', location: 'Shelf A-1', image: '🍪' },
  { id: '2', name: 'Tata Salt 1kg', sku: 'TS-1K', category: 'Essentials', price: 28, mrp: 28, stock: 45, unit: 'pkt', location: 'Shelf B-3', image: '🧂' },
  { id: '3', name: 'Amul Butter 500g', sku: 'AB-500', category: 'Dairy', price: 280, mrp: 290, stock: 8, unit: 'pkt', location: 'Fridge 1', image: '🧈' },
  { id: '4', name: 'Maggi Noodles', sku: 'MGI-2M', category: 'Snacks', price: 14, mrp: 14, stock: 200, unit: 'pkt', location: 'Shelf A-2', image: '🍜' },
  { id: '5', name: 'Surf Excel 1kg', sku: 'SE-1K', category: 'Household', price: 240, mrp: 250, stock: 3, unit: 'pkt', location: 'Shelf D-1', image: '🫧' },
  { id: '6', name: 'Fortune Oil 1L', sku: 'FO-1L', category: 'Essentials', price: 180, mrp: 185, stock: 0, unit: 'btl', location: 'Shelf C-2', image: '🫗' },
  { id: '7', name: 'Britannia Bread', sku: 'BB-400', category: 'Bakery', price: 45, mrp: 45, stock: 15, unit: 'pkt', location: 'Counter', image: '🍞' },
  { id: '8', name: 'Haldiram Namkeen 200g', sku: 'HN-200', category: 'Snacks', price: 55, mrp: 60, stock: 32, unit: 'pkt', location: 'Shelf A-3', image: '🥜' },
  { id: '9', name: 'Clinic Plus Shampoo', sku: 'CPS-ML', category: 'Personal Care', price: 5, mrp: 5, stock: 150, unit: 'sachet', location: 'Shelf E-1', image: '🧴' },
  { id: '10', name: 'Thums Up 750ml', sku: 'TU-750', category: 'Beverages', price: 40, mrp: 42, stock: 24, unit: 'btl', location: 'Fridge 2', image: '🥤' },
  { id: '11', name: 'Aashirvaad Atta 5kg', sku: 'AA-5K', category: 'Essentials', price: 320, mrp: 330, stock: 5, unit: 'bag', location: 'Shelf C-1', image: '🌾' },
  { id: '12', name: 'Cadbury Dairy Milk', sku: 'CDM-RS10', category: 'Snacks', price: 10, mrp: 10, stock: 85, unit: 'pc', location: 'Counter', image: '🍫' },
];

export const mockCategories = ['All', 'Essentials', 'Snacks', 'Dairy', 'Beverages', 'Household', 'Personal Care', 'Bakery'];

export const mockKhataAccounts = [
  { id: '1', name: 'Ramesh Kumar', phone: '9876543210', balance: 2450, lastTransaction: '2026-03-13', image: '👨' },
  { id: '2', name: 'Sunita Devi', phone: '9876543211', balance: 850, lastTransaction: '2026-03-12', image: '👩' },
  { id: '3', name: 'Manoj Sharma', phone: '9876543212', balance: -500, lastTransaction: '2026-03-11', image: '👨‍🦱' },
  { id: '4', name: 'Priya Singh', phone: '9876543213', balance: 3200, lastTransaction: '2026-03-10', image: '👩‍🦰' },
  { id: '5', name: 'Deepak Verma', phone: '9876543214', balance: 120, lastTransaction: '2026-03-13', image: '🧑' },
  { id: '6', name: 'Asha Kumari', phone: '9876543215', balance: 1800, lastTransaction: '2026-03-09', image: '👵' },
];

export const mockKhataEntries = [
  { id: '1', accountId: '1', type: 'gave', amount: 500, note: 'Ration items', date: '2026-03-13T10:30:00' },
  { id: '2', accountId: '1', type: 'received', amount: 200, note: 'Partial payment', date: '2026-03-12T14:00:00' },
  { id: '3', accountId: '1', type: 'gave', amount: 350, note: 'Oil + Sugar', date: '2026-03-10T09:15:00' },
  { id: '4', accountId: '1', type: 'gave', amount: 800, note: 'Monthly ration', date: '2026-03-08T11:00:00' },
  { id: '5', accountId: '1', type: 'received', amount: 1000, note: 'UPI payment', date: '2026-03-05T16:30:00' },
];

export const mockRecentBills = [
  { id: 'B001', customer: 'Walk-in', items: 3, total: 145, time: '10:30 AM', mode: 'UPI' },
  { id: 'B002', customer: 'Ramesh Kumar', items: 7, total: 890, time: '10:15 AM', mode: 'Khata' },
  { id: 'B003', customer: 'Walk-in', items: 1, total: 40, time: '09:50 AM', mode: 'Cash' },
  { id: 'B004', customer: 'Sunita Devi', items: 5, total: 520, time: '09:30 AM', mode: 'UPI' },
  { id: 'B005', customer: 'Walk-in', items: 2, total: 65, time: '09:10 AM', mode: 'Cash' },
];

export const mockSalesData = [
  { day: 'Mon', sales: 4200 },
  { day: 'Tue', sales: 3800 },
  { day: 'Wed', sales: 5100 },
  { day: 'Thu', sales: 4600 },
  { day: 'Fri', sales: 6200 },
  { day: 'Sat', sales: 7800 },
  { day: 'Sun', sales: 3200 },
];

export const mockDashboardStats = {
  todaySales: 12450,
  billsCount: 23,
  newCustomers: 4,
  lowStockItems: 3,
};
