import { useState } from 'react';
import { Store, Phone, Hash, MapPin, Globe, Moon, Sun, ChevronRight, Shield, LogOut } from 'lucide-react';
import { useTheme } from '../context/ThemeContext';
import './Settings.css';

export default function SettingsPage() {
  const [language, setLanguage] = useState('en');
  const { theme, toggleTheme } = useTheme();
  const darkMode = theme === 'dark';

  return (
    <div className="page">
      {/* Header */}
      <div className="page-header animate-fade-up">
        <div>
          <h1 className="page-title">Settings</h1>
          <p className="page-subtitle">Store & app preferences</p>
        </div>
      </div>

      {/* Store Profile */}
      <div className="section animate-fade-up" style={{ animationDelay: '0.05s' }}>
        <h3 className="section-title">Store Profile</h3>
        <div className="settings-card glass-card-static">
          <div className="settings-store-header">
            <div className="settings-store-avatar">🏪</div>
            <div className="settings-store-info">
              <h3 className="settings-store-name">Ramesh Kirana Store</h3>
              <p className="settings-store-role">Owner · Premium Plan</p>
            </div>
          </div>

          <div className="settings-fields">
            <div className="settings-field">
              <div className="settings-field-icon">
                <Store size={16} />
              </div>
              <div className="settings-field-content">
                <span className="settings-field-label">Store Name</span>
                <span className="settings-field-value">Ramesh Kirana Store</span>
              </div>
              <ChevronRight size={16} className="settings-field-arrow" />
            </div>

            <div className="settings-field">
              <div className="settings-field-icon">
                <Phone size={16} />
              </div>
              <div className="settings-field-content">
                <span className="settings-field-label">Phone</span>
                <span className="settings-field-value">+91 98765 43210</span>
              </div>
              <ChevronRight size={16} className="settings-field-arrow" />
            </div>

            <div className="settings-field">
              <div className="settings-field-icon">
                <Hash size={16} />
              </div>
              <div className="settings-field-content">
                <span className="settings-field-label">GST Number</span>
                <span className="settings-field-value">07AAACR5055K1Z5</span>
              </div>
              <ChevronRight size={16} className="settings-field-arrow" />
            </div>

            <div className="settings-field">
              <div className="settings-field-icon">
                <MapPin size={16} />
              </div>
              <div className="settings-field-content">
                <span className="settings-field-label">Address</span>
                <span className="settings-field-value">123, Main Market, Nehru Nagar, Delhi - 110001</span>
              </div>
              <ChevronRight size={16} className="settings-field-arrow" />
            </div>
          </div>
        </div>
      </div>

      {/* App Preferences */}
      <div className="section animate-fade-up" style={{ animationDelay: '0.15s' }}>
        <h3 className="section-title">App Preferences</h3>
        <div className="settings-card glass-card-static">
          {/* Language */}
          <div className="settings-field">
            <div className="settings-field-icon">
              <Globe size={16} />
            </div>
            <div className="settings-field-content">
              <span className="settings-field-label">Language / भाषा</span>
            </div>
            <select className="settings-select" value={language} onChange={e => setLanguage(e.target.value)} id="language-select">
              <option value="en">English</option>
              <option value="hi">हिंदी</option>
              <option value="ta">தமிழ்</option>
              <option value="te">తెలుగు</option>
              <option value="mr">मराठी</option>
            </select>
          </div>

          {/* Theme */}
          <div className="settings-field">
            <div className="settings-field-icon">
              {darkMode ? <Moon size={16} /> : <Sun size={16} />}
            </div>
            <div className="settings-field-content">
              <span className="settings-field-label">Dark Mode</span>
            </div>
            <button
              className={`settings-toggle ${darkMode ? 'settings-toggle-on' : ''}`}
              onClick={toggleTheme}
              id="dark-mode-toggle"
            >
              <div className="settings-toggle-thumb" />
            </button>
          </div>
        </div>
      </div>

      {/* Security & Account */}
      <div className="section animate-fade-up" style={{ animationDelay: '0.25s' }}>
        <h3 className="section-title">Account</h3>
        <div className="settings-card glass-card-static">
          <div className="settings-field">
            <div className="settings-field-icon">
              <Shield size={16} />
            </div>
            <div className="settings-field-content">
              <span className="settings-field-label">Privacy & Security</span>
            </div>
            <ChevronRight size={16} className="settings-field-arrow" />
          </div>

          <button className="settings-field settings-logout" id="logout-btn">
            <div className="settings-field-icon settings-logout-icon">
              <LogOut size={16} />
            </div>
            <div className="settings-field-content">
              <span className="settings-field-label settings-logout-label">Log Out</span>
            </div>
          </button>
        </div>
      </div>

      {/* Version */}
      <div className="settings-version animate-fade-up" style={{ animationDelay: '0.3s' }}>
        <p>RetailOS v0.1.0</p>
        <p>Made with 🇮🇳 for Kirana Stores</p>
      </div>
    </div>
  );
}
