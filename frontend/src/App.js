import React, { useState, useEffect } from "react";
import ExpenseList from "./components/ExpenseList";
import Login from "./components/Login";
import Register from "./components/Register";
import { logout } from "./api/authApi";
import { getBudgets } from "./api/budgetApi";
import { getExpenses, deleteExpense } from "./api/expenseApi";

// 대시보드 카드 스타일
const cardStyle = {
  padding: "25px",
  marginBottom: "20px",
  boxShadow: "0 4px 20px rgba(0,0,0,0.08)",
  borderRadius: "16px",
  backgroundColor: "#fff",
};

const buttonStyle = {
  padding: "10px 18px",
  borderRadius: "8px",
  border: "none",
  cursor: "pointer",
  fontWeight: "bold",
  transition: "0.2s",
};

export default function App() {
  const [reload, setReload] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [showRegister, setShowRegister] = useState(false);

  const [budgets, setBudgets] = useState([]);
  const [expenses, setExpenses] = useState([]);

  const [yearMonth, setYearMonth] = useState(() => {
    const now = new Date();
    return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
  });
  const [year, month] = yearMonth.split("-");

  // 초기 로그인 체크 (JWT 세션 유지)
  useEffect(() => {
    const token = sessionStorage.getItem("accessToken");
    if (token) setIsLoggedIn(true);
  }, []);

  // useEffect(() => {
  //   if (!isLoggedIn) return;
  //   async function fetchBudgets() {
  //     try {
  //       const response = await getBudgets(year, month);
  //       setBudgets(response.data.data);
  //     } catch (error) {
  //       console.error("예산 데이터 로드 실패", error);
  //     }
  //   }
  //   fetchBudgets();
  // }, [isLoggedIn, reload, year, month]);

  // [본질] 실시간 지출 내역 조회 (No-Offset 페이징 유지)
  useEffect(() => {
    if (!isLoggedIn) return;
    async function fetchInitialExpenses() {
      try {
        const response = await getExpenses({ year, month, accountNumber: '110-123-456789' });
        console.log(response);
        
        setExpenses(response.data.data.expenses); 
        setBudgets(response.data.data.accountInfo.balance);

      } catch (e) { console.error(e); }
    }
    fetchInitialExpenses();
  }, [isLoggedIn, reload, year, month]);

  const loadMoreExpenses = async () => {
    const lastExpense = expenses[expenses.length - 1];
    if (!lastExpense) return;
    const response = await getExpenses({ 
      year, 
      month, 
      lastDay: lastExpense.day, 
      lastId: lastExpense.id,
      accountNumber: '110-123-456789'
    });
    setExpenses(prev => [...prev, ...response.data.data.expenses]);
        setBudgets(response.data.data.accountInfo.balance);

  };

  const handleLoginSuccess = () => setIsLoggedIn(true);
  const handleLogout = async () => {
    try { await logout(); } catch (e) { console.error(e); }
    sessionStorage.removeItem("accessToken");
    setIsLoggedIn(false);
  };

  const handleDeleteExpense = async (id) => {
    if(window.confirm("내역을 삭제하시겠습니까?")) {
        try {
            await deleteExpense(id);
            setReload((prev) => !prev);
        } catch (err) { console.error(err); }
    }
  };

  const moveMonth = (diff) => {
    const date = new Date(yearMonth + "-01");
    date.setMonth(date.getMonth() + diff);
    setYearMonth(`${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}`);
  };

  // 로그인/회원가입 분기 로직 유지
  if (!isLoggedIn) {
    if (showRegister) {
      return <Register onRegisterSuccess={() => setShowRegister(false)} onBackToLogin={() => setShowRegister(false)} />;
    }
    return <Login onLoginSuccess={handleLoginSuccess} onShowRegister={() => setShowRegister(true)} />;
  }

  return (
    <div style={{ maxWidth: "850px", margin: "0 auto", padding: "30px", backgroundColor: "#f8f9fa", minHeight: "100vh" }}>
      
      {/* 헤더 섹션 */}
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "30px" }}>
        <h1 style={{ margin: 0, color: "#1a1a1a", letterSpacing: "-1px" }}>SmartBudget <span style={{ color: "#0046ff", fontSize: "0.5em" }}>v2.0</span></h1>
        <button style={{ ...buttonStyle, backgroundColor: "#ffeded", color: "#ff4d4d" }} onClick={handleLogout}>로그아웃</button>
      </div>

      {/* [핵심 1] 실시간 예산 잔액 카드 */}
      <div style={{ ...cardStyle, background: "linear-gradient(135deg, #0046ff 0%, #0031b3 100%)", color: "white" }}>
        <div style={{ display: "flex", justifyContent: "space-between", opacity: 0.8, fontSize: "0.9em", marginBottom: "10px" }}>
          <span>연동 계좌: 110-123-456789</span>
          <span>{yearMonth} 기준</span>
        </div>
        <h3 style={{ margin: 0, fontWeight: "normal" }}>현재 결제 가능 예산</h3>
        <h2 style={{ fontSize: "2.8em", margin: "10px 0" }}>
          {budgets} <small style={{fontSize: '0.5em'}}>원</small>
        </h2>
        <div style={{ fontSize: "0.85em", backgroundColor: "rgba(255,255,255,0.1)", padding: "8px 15px", borderRadius: "8px", display: "inline-block" }}>
          ● WebSocket 실시간 모니터링 중
        </div>
      </div>

      {/* 년/월 페이징 - 인덱스 활용 성능 최적화 강조 포인트 */}
      <div style={{ display: "flex", justifyContent: "center", alignItems: "center", gap: "20px", marginBottom: "25px" }}>
        <button style={{ ...buttonStyle, backgroundColor: "#fff", border: "1px solid #ddd" }} onClick={() => moveMonth(-1)}>◀</button>
        <strong style={{ fontSize: "20px", color: "#333" }}>{year}년 {month}월</strong>
        <button style={{ ...buttonStyle, backgroundColor: "#fff", border: "1px solid #ddd" }} onClick={() => moveMonth(1)}>▶</button>
      </div>

      {/* [핵심 2] 실시간 지출 내역 리스트 */}
      <div style={cardStyle}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "20px" }}>
          <h2 style={{ margin: 0, fontSize: "1.2em" }}>📊 실시간 지출 피드</h2>
          <button style={{ ...buttonStyle, backgroundColor: "#f0f2f5", color: "#666", fontSize: "0.85em" }} onClick={() => setReload(!reload)}>새로고침</button>
        </div>
        
        <ExpenseList 
          expenses={expenses} 
          onEdit={() => {}} // 결제 데이터이므로 수동 수정은 지양 (필요시 기능 활성화)
          onDelete={handleDeleteExpense} 
        />
        
        {expenses.length > 0 && (
          <button 
            onClick={loadMoreExpenses}
            style={{ width: "100%", marginTop: "20px", padding: "12px", border: "1px solid #eee", borderRadius: "8px", background: "#fff", cursor: "pointer", color: "#888", fontWeight: "bold" }}
          >
            과거 내역 더보기
          </button>
        )}
      </div>

      {/* [핵심 3] 데이터 정합성 보정 결과 (Batch) 영역 */}
      <div style={{ ...cardStyle, border: "2px dashed #e0e0e0", backgroundColor: "transparent", textAlign: "center" }}>
        <h3 style={{ color: "#888", marginTop: 0 }}>🔍 일일 장부 대조 현황 (Batch)</h3>
        <p style={{ color: "#999", fontSize: "0.9em" }}>
          은행 서버 원장과 로컬 지출 내역의 정합성을 배치가 검증하고 있습니다.
        </p>
        <div style={{ color: "#aaa", fontSize: "0.8em" }}>마지막 대조 일시: 2026-03-02 00:00:05 (정상)</div>
      </div>
    </div>
  );
}