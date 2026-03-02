// src/api/budgetApi.js
import api from "./api";

export const getBudgets = () => api.get("/budgets/search");
export const createBudget = (budget) => api.post("/budgets", budget);
export const updateBudget = (budget) => api.put("/budgets", budget);
export const deleteBudgetByIdAndUserId = (id) => api.delete(`/budgets/${id}`);
