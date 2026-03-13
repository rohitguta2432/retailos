import { useState } from 'react';
import {
  TrendingUp, ShoppingBag, Users, AlertTriangle,
  ArrowUpRight, IndianRupee, Bell, Search
} from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, ResponsiveContainer, Tooltip } from 'recharts';
import { mockDashboardStats, mockSalesData, mockRecentBills, mockProducts } from '../data/mockData';
import './Dashboard.css';

const formatCurrency = (amount) => `₹${amount.toLocaleString('en-IN')}`;

function StatCard({ icon: Icon, label, value, trend, color, delay }) {
  return (
    <div className="stat-card" style={{ animationDelay: `${delay}s`, '--stat-color': color }}>
      <div className="stat-icon-wrap">
        <Icon size={20} />
      </div>
      <div className="stat-info">
        <span className="stat-value">{value}</span>
        <span className="stat-label">{label}</span>
      </div>
      {trend && (
        <span className="stat-trend">
          <ArrowUpRight size={14} />
          {trend}
        </span>
      )}
    </div>
  );
}

const CustomTooltip = ({ active, payload }) => {
  if (active && payload && payload.length) {
    return (
      <div className="chart-tooltip">
        <span>{formatCurrency(payload[0].value)}</span>
      </div>
    );
  }
  return null;
};

export default function Dashboard() {
  const lowStockProducts = mockProducts.filter(p => p.stock <= 5);

  return (
    <div className="page">
      {/* Header */}
      <div className="dash-header animate-fade-up">
        <div>
          <p className="dash-greeting">Good Morning 🙏</p>
          <h1 className="dash-store-name">Ramesh Kirana Store</h1>
        </div>
        <div className="dash-header-actions">
          <button className="btn-icon" id="search-btn">
            <Search size={20} />
          </button>
          <button className="btn-icon notification-btn" id="notifications-btn">
            <Bell size={20} />
            <span className="notification-dot"></span>
          </button>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="stats-grid stagger-children">
        <StatCard
          icon={IndianRupee}
          label="Today's Sales"
          value={formatCurrency(mockDashboardStats.todaySales)}
          trend="+12%"
          color="var(--success)"
          delay={0}
        />
        <StatCard
          icon={ShoppingBag}
          label="Bills Created"
          value={mockDashboardStats.billsCount}
          trend="+3"
          color="var(--primary-light)"
          delay={0.05}
        />
        <StatCard
          icon={Users}
          label="New Customers"
          value={mockDashboardStats.newCustomers}
          color="var(--accent)"
          delay={0.1}
        />
        <StatCard
          icon={AlertTriangle}
          label="Low Stock"
          value={mockDashboardStats.lowStockItems}
          color="var(--danger)"
          delay={0.15}
        />
      </div>

      {/* Sales Chart */}
      <div className="section animate-fade-up" style={{ animationDelay: '0.2s' }}>
        <h3 className="section-title">This Week's Sales</h3>
        <div className="glass-card-static chart-card">
          <ResponsiveContainer width="100%" height={180}>
            <BarChart data={mockSalesData} barSize={24}>
              <XAxis
                dataKey="day"
                axisLine={false}
                tickLine={false}
                tick={{ fill: '#64748b', fontSize: 12 }}
              />
              <YAxis hide />
              <Tooltip content={<CustomTooltip />} cursor={{ fill: 'rgba(99,102,241,0.08)' }} />
              <Bar
                dataKey="sales"
                fill="url(#barGradient)"
                radius={[6, 6, 0, 0]}
              />
              <defs>
                <linearGradient id="barGradient" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stopColor="#818cf8" />
                  <stop offset="100%" stopColor="#4f46e5" />
                </linearGradient>
              </defs>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="section animate-fade-up" style={{ animationDelay: '0.25s' }}>
        <h3 className="section-title">Quick Actions</h3>
        <div className="quick-actions">
          <button className="quick-action-btn" id="quick-new-bill">
            <span className="qa-icon qa-icon-bill">🧾</span>
            <span>New Bill</span>
          </button>
          <button className="quick-action-btn" id="quick-add-product">
            <span className="qa-icon qa-icon-product">📦</span>
            <span>Add Product</span>
          </button>
          <button className="quick-action-btn" id="quick-khata">
            <span className="qa-icon qa-icon-khata">📒</span>
            <span>Khata</span>
          </button>
          <button className="quick-action-btn" id="quick-reports">
            <span className="qa-icon qa-icon-reports">📊</span>
            <span>Reports</span>
          </button>
        </div>
      </div>

      {/* Low Stock Alerts */}
      {lowStockProducts.length > 0 && (
        <div className="section animate-fade-up" style={{ animationDelay: '0.3s' }}>
          <h3 className="section-title">⚠️ Low Stock Alerts</h3>
          <div className="low-stock-list stagger-children">
            {lowStockProducts.map(product => (
              <div key={product.id} className="low-stock-item glass-card-static">
                <span className="low-stock-emoji">{product.image}</span>
                <div className="low-stock-info">
                  <span className="low-stock-name">{product.name}</span>
                  <span className="low-stock-location">{product.location}</span>
                </div>
                <span className={`badge ${product.stock === 0 ? 'badge-danger' : 'badge-warning'}`}>
                  {product.stock === 0 ? 'Out of Stock' : `${product.stock} left`}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Recent Bills */}
      <div className="section animate-fade-up" style={{ animationDelay: '0.35s' }}>
        <h3 className="section-title">Recent Bills</h3>
        <div className="recent-bills stagger-children">
          {mockRecentBills.map(bill => (
            <div key={bill.id} className="bill-item glass-card-static">
              <div className="bill-main">
                <span className="bill-id">#{bill.id}</span>
                <span className="bill-customer">{bill.customer}</span>
              </div>
              <div className="bill-meta">
                <span className="bill-time">{bill.time}</span>
                <span className={`badge ${bill.mode === 'UPI' ? 'badge-info' : bill.mode === 'Khata' ? 'badge-warning' : 'badge-success'}`}>
                  {bill.mode}
                </span>
              </div>
              <span className="bill-total">{formatCurrency(bill.total)}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
