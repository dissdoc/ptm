Ptm::Application.routes.draw do
  devise_for :users

  resources :albums do
    get 'my', :on => :collection

    resources :photos do
      resources :notes
    end
  end

  root :to => 'home#index'
  match 'users', :to => 'home#users'
end
