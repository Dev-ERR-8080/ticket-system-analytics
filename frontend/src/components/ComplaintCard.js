import React from 'react';
import { useNavigate } from 'react-router-dom';
import './ComplaintCard.css';

const ComplaintCard = ({ complaint }) => {
  const navigate = useNavigate();

  const getStatusClass = (status) => {
    switch (status) {
      case 'OPEN':
        return 'status-open';
      case 'IN_PROGRESS':
        return 'status-in-progress';
      case 'RESOLVED':
        return 'status-resolved';
      default:
        return '';
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  const handleClick = () => {
    navigate(`/complaint/${complaint.id}`);
  };

  return (
    <tr onClick={handleClick} style={{ cursor: 'pointer' }}>
      <td>{complaint.id}</td>
      <td>{complaint.title}</td>
      <td>{complaint.category}</td>
      <td>{complaint.assignedTo}</td>
      <td>
        <span className={`status-badge ${getStatusClass(complaint.status)}`}>
          {complaint.status.replace('_', ' ')}
        </span>
      </td>
      <td>{formatDate(complaint.createdAt)}</td>
    </tr>
  );
};

export default ComplaintCard;
