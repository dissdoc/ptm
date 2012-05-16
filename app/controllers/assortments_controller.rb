class AssortmentsController < ApplicationController
  before_filter :authenticate_user!

  def index
    @title_page = 'My collections'
    add_breadcrumb @title_page, ''
  end

  def new
    @title_page = 'Create new collection'
    add_breadcrumb 'My collections', assortments_path
    add_breadcrumb @title_page, ''

    @assortment = current_user.assortments.new
  end

  def create
    @assortment = current_user.assortments.new(params[:assortment])
    respond_to do |format|
      if @assortment.save
        flash[:notice] = 'Form created successfully'
        format.html { redirect_to assortments_path }
      else
        format.html { render :action => :new }
      end
    end
  end

  def show
    @assortment = current_user.assortments.find(params[:id])
    @title_page = @assortment.name
    add_breadcrumb 'My collection', assortments_path
    add_breadcrumb @title_page, ''
  end
end