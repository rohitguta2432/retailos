import { useState } from 'react';
import { Search, Plus, ArrowDownLeft, ArrowUpRight, Phone, MessageCircle, X, IndianRupee } from 'lucide-react';
import { mockKhataAccounts, mockKhataEntries } from '../data/mockData';
import './Khata.css';

const formatCurrency = (amount) => `₹${Math.abs(amount).toLocaleString('en-IN')}`;

export default function Khata() {
  const [search, setSearch] = useState('');
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [showAddEntry, setShowAddEntry] = useState(false);
  const [entryType, setEntryType] = useState('gave');

  const totalOutstanding = mockKhataAccounts.reduce((sum, a) => sum + Math.max(0, a.balance), 0);

  const filtered = mockKhataAccounts.filter(a =>
    a.name.toLowerCase().includes(search.toLowerCase()) ||
    a.phone.includes(search)
  );

  if (selectedAccount) {
    const account = mockKhataAccounts.find(a => a.id === selectedAccount);
    return (
      <div className="page">
        {/* Account Detail Header */}
        <div className="khata-detail-header animate-fade-up">
          <button className="btn-ghost" onClick={() => setSelectedAccount(null)}>← Back</button>
          <div className="khata-detail-profile">
            <span className="khata-avatar-lg">{account.image}</span>
            <h2 className="khata-detail-name">{account.name}</h2>
            <p className="khata-detail-phone">{account.phone}</p>
          </div>
        </div>

        {/* Balance Card */}
        <div className={`khata-balance-card glass-card-static animate-fade-up ${account.balance > 0 ? 'khata-balance-owed' : 'khata-balance-clear'}`}>
          <span className="khata-balance-label">
            {account.balance > 0 ? 'You will receive' : account.balance < 0 ? 'You will give' : 'Settled up'}
          </span>
          <span className="khata-balance-amount">{formatCurrency(account.balance)}</span>
        </div>

        {/* Action Buttons */}
        <div className="khata-actions animate-fade-up" style={{ animationDelay: '0.1s' }}>
          <button className="btn btn-primary" onClick={() => { setEntryType('gave'); setShowAddEntry(true); }} id="khata-gave-btn">
            <ArrowUpRight size={16} />
            You Gave
          </button>
          <button className="btn btn-outline" style={{ color: 'var(--success)' }} onClick={() => { setEntryType('received'); setShowAddEntry(true); }} id="khata-received-btn">
            <ArrowDownLeft size={16} />
            You Received
          </button>
        </div>

        {/* Contact Actions */}
        <div className="khata-contact-actions animate-fade-up" style={{ animationDelay: '0.15s' }}>
          <button className="btn btn-ghost btn-sm">
            <Phone size={14} />
            Call
          </button>
          <button className="btn btn-ghost btn-sm" style={{ color: '#25D366' }}>
            <MessageCircle size={14} />
            Send Reminder via WhatsApp
          </button>
        </div>

        {/* Transaction History */}
        <div className="section animate-fade-up" style={{ animationDelay: '0.2s' }}>
          <h3 className="section-title">Transaction History</h3>
          <div className="khata-timeline stagger-children">
            {mockKhataEntries.map(entry => (
              <div key={entry.id} className="khata-entry glass-card-static">
                <div className={`khata-entry-icon ${entry.type === 'gave' ? 'khata-entry-gave' : 'khata-entry-received'}`}>
                  {entry.type === 'gave' ? <ArrowUpRight size={16} /> : <ArrowDownLeft size={16} />}
                </div>
                <div className="khata-entry-info">
                  <span className="khata-entry-note">{entry.note}</span>
                  <span className="khata-entry-date">
                    {new Date(entry.date).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })}
                  </span>
                </div>
                <span className={`khata-entry-amount ${entry.type === 'gave' ? 'khata-amount-gave' : 'khata-amount-received'}`}>
                  {entry.type === 'gave' ? '-' : '+'}{formatCurrency(entry.amount)}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* Add Entry Modal */}
        {showAddEntry && (
          <div className="modal-overlay" onClick={() => setShowAddEntry(false)}>
            <div className="modal-content" onClick={e => e.stopPropagation()}>
              <div className="modal-header">
                <h2 className="modal-title">
                  {entryType === 'gave' ? 'You Gave' : 'You Received'}
                </h2>
                <button className="btn-icon" onClick={() => setShowAddEntry(false)}>
                  <X size={20} />
                </button>
              </div>
              <div className="form-group">
                <label className="form-label">Amount (₹)</label>
                <input className="input khata-amount-input" type="number" placeholder="0" autoFocus id="khata-amount-input" />
              </div>
              <div className="form-group">
                <label className="form-label">Note (optional)</label>
                <input className="input" placeholder="e.g. Ration items" id="khata-note-input" />
              </div>
              <button
                className={`btn ${entryType === 'gave' ? 'btn-primary' : 'btn-accent'}`}
                style={{ width: '100%' }}
                id="save-khata-entry"
              >
                Save Entry
              </button>
            </div>
          </div>
        )}
      </div>
    );
  }

  return (
    <div className="page">
      {/* Header */}
      <div className="page-header animate-fade-up">
        <div>
          <h1 className="page-title">Khata Book</h1>
          <p className="page-subtitle">{mockKhataAccounts.length} accounts</p>
        </div>
        <button className="btn btn-primary btn-sm" id="add-customer-btn">
          <Plus size={16} />
          Add
        </button>
      </div>

      {/* Outstanding Card */}
      <div className="khata-outstanding glass-card-static animate-fade-up" style={{ animationDelay: '0.05s' }}>
        <div className="khata-outstanding-label">
          <IndianRupee size={16} />
          <span>Total Outstanding</span>
        </div>
        <span className="khata-outstanding-amount">{formatCurrency(totalOutstanding)}</span>
      </div>

      {/* Search */}
      <div className="inv-search-bar animate-fade-up" style={{ animationDelay: '0.1s' }}>
        <div className="inv-search-input-wrap">
          <Search size={18} className="inv-search-icon" />
          <input
            className="input inv-search-input"
            placeholder="Search customer name or phone..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            id="khata-search"
          />
        </div>
      </div>

      {/* Accounts List */}
      <div className="khata-accounts stagger-children">
        {filtered.map(account => (
          <button
            key={account.id}
            className="khata-account glass-card-static"
            onClick={() => setSelectedAccount(account.id)}
            id={`khata-account-${account.id}`}
          >
            <span className="khata-avatar">{account.image}</span>
            <div className="khata-account-info">
              <span className="khata-account-name">{account.name}</span>
              <span className="khata-account-phone">{account.phone}</span>
            </div>
            <div className="khata-account-balance">
              <span className={`khata-balance-value ${account.balance > 0 ? 'khata-will-get' : account.balance < 0 ? 'khata-will-give' : ''}`}>
                {account.balance > 0 ? `+${formatCurrency(account.balance)}` : account.balance < 0 ? `-${formatCurrency(account.balance)}` : 'Settled'}
              </span>
              <span className="khata-balance-sub">
                {account.balance > 0 ? 'will receive' : account.balance < 0 ? 'will give' : '✓'}
              </span>
            </div>
          </button>
        ))}
      </div>
    </div>
  );
}
