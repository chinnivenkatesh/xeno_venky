import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useEffect, useState } from 'react';
import {
  Area,
  AreaChart,
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Legend,
  Line,
  LineChart,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis, YAxis
} from 'recharts';

// API URL - uses environment variable in production, localhost in development
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';


function App() {
  const [stats, setStats] = useState({ totalCustomers: 0, totalOrders: 0, totalRevenue: 0 });
  const [orders, setOrders] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState('sample'); // 'sample' or 'shopify'
  const [syncMessage, setSyncMessage] = useState('');

  // Process data for different charts
  const getOrderStatusData = () => {
    if (!orders.length) return [];
    const statusCount = {};
    orders.forEach(order => {
      statusCount[order.financialStatus] = (statusCount[order.financialStatus] || 0) + 1;
    });
    return Object.entries(statusCount).map(([status, count]) => ({
      name: status.charAt(0).toUpperCase() + status.slice(1),
      value: count
    }));
  };

  const getDailyRevenueData = () => {
    if (!orders.length) return [];
    const dailyData = {};
    orders.forEach(order => {
      const date = new Date(order.processedAt).toLocaleDateString();
      dailyData[date] = (dailyData[date] || 0) + order.totalPrice;
    });
    return Object.entries(dailyData).map(([date, revenue]) => ({
      date,
      revenue: parseFloat(revenue.toFixed(2))
    })).sort((a, b) => new Date(a.date) - new Date(b.date));
  };

  const getOrderTrendData = () => {
    if (!orders.length) return [];
    const trendData = {};
    orders.forEach(order => {
      const date = new Date(order.processedAt).toLocaleDateString();
      trendData[date] = (trendData[date] || 0) + 1;
    });
    return Object.entries(trendData).map(([date, count]) => ({
      date,
      orders: count
    })).sort((a, b) => new Date(a.date) - new Date(b.date));
  };

  const getCustomerSpendData = () => {
    if (!customers.length) return [];
    return customers
      .sort((a, b) => b.totalSpent - a.totalSpent)
      .slice(0, 10)
      .map(c => ({
        name: `${c.firstName} ${c.lastName}`,
        spent: parseFloat(c.totalSpent.toFixed(2))
      }));
  };

  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8'];

  // 1. Fetch Data from Java Backend
  const fetchData = async () => {
    try {
      const statsRes = await axios.get(`${API_BASE_URL}/api/dashboard-stats`);
      setStats(statsRes.data);

      const ordersRes = await axios.get(`${API_BASE_URL}/api/orders`);
      setOrders(ordersRes.data);

      const customersRes = await axios.get(`${API_BASE_URL}/api/customers`);
      setCustomers(customersRes.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  // 2. Button to Trigger Shopify Sync
  const handleSync = async () => {
    setLoading(true);
    setSyncMessage('');
    try {
      const response = await axios.post(`${API_BASE_URL}/api/sync`);
      const message = response.data || "Sync completed";

      // Check if sync was successful
      if (message.includes("Successful") || message.includes("successful")) {
        setDataSource('shopify');
        setSyncMessage('✅ Data synced from Shopify');
      } else {
        setSyncMessage('⚠️ ' + message);
      }

      // Wait a moment then fetch fresh data
      setTimeout(() => {
        fetchData();
      }, 1000);
    } catch (error) {
      const errorMsg = error.response?.data || error.message || "Unknown error occurred";
      setDataSource('sample');
      setSyncMessage('❌ Sync failed: ' + errorMsg);
    }
    setLoading(false);
  };

  // Load data when page opens
  useEffect(() => {
    fetchData();
  }, []);

  return (
    <div className="container-fluid mt-5 mb-5" style={{ backgroundColor: '#f8f9fa', padding: '20px' }}>
      {/* Data Source Banner */}
      <div className={`alert mb-4 ${dataSource === 'shopify' ? 'alert-success' : 'alert-info'}`}>
        <strong>📊 Data Source: </strong>
        {dataSource === 'shopify' ? '✅ Live Shopify Store' : '📦 Sample Data (Local)'}
        {syncMessage && <div className="mt-2">{syncMessage}</div>}
      </div>

      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1>🛍️ Xeno Store Insights</h1>
        <button
          className="btn btn-primary btn-lg"
          onClick={handleSync}
          disabled={loading}
        >
          {loading ? 'Syncing...' : '🔄 Sync Shopify Data'}
        </button>
      </div>

      {/* Stats Cards */}
      <div className="row text-center mb-5">
        <div className="col-md-4 mb-3">
          <div className="card p-4 shadow-sm border-primary" style={{ borderLeft: '5px solid #0088FE' }}>
            <h5 className="text-muted">👥 Total Customers</h5>
            <h2 className="display-4 text-primary">{stats.totalCustomers}</h2>
          </div>
        </div>
        <div className="col-md-4 mb-3">
          <div className="card p-4 shadow-sm border-success" style={{ borderLeft: '5px solid #00C49F' }}>
            <h5 className="text-muted">💰 Total Revenue</h5>
            <h2 className="display-4 text-success">${stats.totalRevenue.toFixed(2)}</h2>
          </div>
        </div>
        <div className="col-md-4 mb-3">
          <div className="card p-4 shadow-sm border-info" style={{ borderLeft: '5px solid #FFBB28' }}>
            <h5 className="text-muted">📦 Total Orders</h5>
            <h2 className="display-4 text-warning">{stats.totalOrders}</h2>
          </div>
        </div>
      </div>

      {/* Chart Row 1: Bar Chart & Pie Chart */}
      <div className="row mb-4">
        {/* Order Values Bar Chart */}
        <div className="col-md-6 mb-4">
          <div className="card p-4 shadow-sm">
            <h5 className="mb-3">📊 Order Values Distribution</h5>
            <div style={{ width: '100%', height: 300 }}>
              <ResponsiveContainer>
                <BarChart data={orders.slice(0, 15)}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="shopifyOrderId" tick={{ fontSize: 12 }} />
                  <YAxis label={{ value: 'Amount ($)', angle: -90, position: 'insideLeft' }} />
                  <Tooltip formatter={(value) => `$${value.toFixed(2)}`} />
                  <Bar dataKey="totalPrice" fill="#8884d8" name="Order Value" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>

        {/* Order Status Pie Chart */}
        <div className="col-md-6 mb-4">
          <div className="card p-4 shadow-sm">
            <h5 className="mb-3">🥧 Order Status Breakdown</h5>
            <div style={{ width: '100%', height: 300 }}>
              <ResponsiveContainer>
                <PieChart>
                  <Pie
                    data={getOrderStatusData()}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={(entry) => `${entry.name}: ${entry.value}`}
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="value"
                  >
                    {getOrderStatusData().map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>
      </div>

      {/* Chart Row 2: Line Chart & Area Chart */}
      <div className="row mb-4">
        {/* Daily Revenue Line Chart */}
        <div className="col-md-6 mb-4">
          <div className="card p-4 shadow-sm">
            <h5 className="mb-3">📈 Daily Revenue Trend</h5>
            <div style={{ width: '100%', height: 300 }}>
              <ResponsiveContainer>
                <LineChart data={getDailyRevenueData()}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" tick={{ fontSize: 11 }} angle={-45} height={80} />
                  <YAxis label={{ value: 'Revenue ($)', angle: -90, position: 'insideLeft' }} />
                  <Tooltip formatter={(value) => `$${value.toFixed(2)}`} />
                  <Legend />
                  <Line type="monotone" dataKey="revenue" stroke="#00C49F" strokeWidth={2} dot={{ r: 4 }} name="Revenue" />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>

        {/* Order Trend Area Chart */}
        <div className="col-md-6 mb-4">
          <div className="card p-4 shadow-sm">
            <h5 className="mb-3">📉 Daily Order Count Trend</h5>
            <div style={{ width: '100%', height: 300 }}>
              <ResponsiveContainer>
                <AreaChart data={getOrderTrendData()}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" tick={{ fontSize: 11 }} angle={-45} height={80} />
                  <YAxis label={{ value: 'Orders', angle: -90, position: 'insideLeft' }} />
                  <Tooltip />
                  <Legend />
                  <Area type="monotone" dataKey="orders" fill="#8884d8" stroke="#0088FE" name="Orders" />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>
      </div>

      {/* Chart Row 3: Top Customers Bar Chart */}
      <div className="row mb-4">
        <div className="col-12">
          <div className="card p-4 shadow-sm">
            <h5 className="mb-3">⭐ Top 10 Customers by Spending</h5>
            <div style={{ width: '100%', height: 350 }}>
              <ResponsiveContainer>
                <BarChart data={getCustomerSpendData()} layout="vertical" margin={{ left: 150, right: 30 }}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis type="number" label={{ value: 'Total Spent ($)', position: 'insideBottomRight', offset: -5 }} />
                  <YAxis dataKey="name" type="category" width={140} tick={{ fontSize: 12 }} />
                  <Tooltip formatter={(value) => `$${value.toFixed(2)}`} />
                  <Bar dataKey="spent" fill="#FFBB28" name="Amount Spent" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;