Ptm::Application.routes.draw do
  devise_for :users

  resources :albums do
    get 'my', :on => :collection

    resources :photos
  end

  root :to => 'home#index'
end
